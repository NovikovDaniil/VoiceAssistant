package com.example.kotlinvoiceassistent.service

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.kotlinvoiceassistent.R
import com.example.kotlinvoiceassistent.model.Forecast
import com.example.kotlinvoiceassistent.model.Geolocation
import com.example.kotlinvoiceassistent.model.NumberString
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.lang.StringBuilder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*
import java.util.regex.Pattern
import java.util.stream.Collectors
import java.util.function.Function;


class AI {
    private lateinit var context: Context
    private var answers: HashMap<String, Function<String, Observable<String>>>

    private var behaviorSubject: BehaviorSubject<String?>? = null

    @RequiresApi(Build.VERSION_CODES.O)
    constructor(context: Context) {
        this.context = context
        behaviorSubject = BehaviorSubject.create()
        answers =
            HashMap()
        answers[context.getString(R.string.input_hello)] =
            Function<String, Observable<String>> { input: String? ->
                Observable.just(
                    context.getString(R.string.output_hello)
                )
            }
        answers[context.getString(R.string.input_howareyou)] =
            Function<String, Observable<String>> { input: String? ->
                Observable.just(
                    context.getString(R.string.output_howareyou)
                )
            }
        answers[context.getString(R.string.input_whatareyoudoing)] =
            Function<String, Observable<String>> { input: String? ->
                Observable.just(
                    context.getString(R.string.output_whatareyoudoing)
                )
            }

        answers[context.getString(R.string.input_whatdayistoday)] =
            Function<String, Observable<String>> { input: String? ->
                Observable.just(
                    String.format(
                        "%1\$td %1\$tB %1\$tY",
                        LocalDate.now()
                    )
                )
            }
        answers[context.getString(R.string.input_whattimeisitnow)] =
            Function<String, Observable<String>> { input: String? ->
                Observable.just(
                    String.format(
                        "%1\$tR",
                        LocalDate.now()
                    )
                )
            }
        answers[context.getString(R.string.input_whatdayoftheweekisit)] =
            Function<String, Observable<String>> { input: String? ->
                Observable.just(
                    String.format(
                        "%1\$tA",
                        LocalDateTime.now()
                    )
                )
            }
        answers[context.getString(R.string.input_howmanydaysuntil)] =
            Function { input: String ->
                Observable.create(
                    ObservableOnSubscribe { emitter: ObservableEmitter<LocalDate> ->
                        val targetDate: LocalDate
                        try {
                            targetDate = try {
                                LocalDate.parse(
                                    input.substring(context.getString(R.string.input_howmanydaysuntil).length)
                                        .trim { it <= ' ' },
                                    DateTimeFormatter.ofPattern("dd.MM.yyyy")
                                )
                            } catch (e: DateTimeParseException) {
                                LocalDate.parse(
                                    input.substring(context.getString(R.string.input_howmanydaysuntil).length)
                                        .trim { it <= ' ' },
                                    DateTimeFormatter.ofPattern("dd MM yyyy")
                                )
                            }
                            emitter.onNext(targetDate)
                            emitter.onComplete()
                        } catch (e: DateTimeParseException) {
                            emitter.onError(Exception(context.getString(R.string.output_howmanydaysuntil_incorrect)))
                        }
                    } as ObservableOnSubscribe<LocalDate>
                )
                    .map { date: LocalDate ->
                        val currentDate = LocalDate.now()
                        if (currentDate.isEqual(date)) {
                            return@map context.getString(R.string.output_howmanydaysuntil_today)
                        } else if (date.isAfter(currentDate)) {
                            return@map String.format(
                                "%s %d",
                                context.getString(R.string.output_howmanydaysuntil),
                                currentDate.until(date).days
                            )
                        } else {
                            return@map context.getString(R.string.output_howmanydaysuntil_passed)
                        }
                    }
            }
        answers[context.getString(R.string.input_whatistheweather)] =
            Function { input: String ->
                Observable.create(
                    ObservableOnSubscribe { emitter: ObservableEmitter<String> ->
                        val city =
                            input.substring(context.getString(R.string.input_whatistheweather).length)
                                .trim { it <= ' ' }
                        if (!city.isEmpty()) {
                            emitter.onNext(city)
                            emitter.onComplete()
                        } else {
                            emitter.onError(Exception(context.getString(R.string.output_whatistheweather_incorrect)))
                        }
                    }
                )
                    .flatMap(ForecastService::getForecast)
                    .map { forecast: Forecast ->
                        try {
                            if (Locale.getDefault() == Locale.forLanguageTag("ru-RU")) {
                                val temperatureSuffix: String
                                temperatureSuffix =
                                    if (Math.abs(forecast.current.temperature) % 100 / 10 !== 1 && Math.abs(
                                            forecast.current.temperature
                                        ) % 10 >= 2 && Math.abs(forecast.current.temperature) % 10 <= 4
                                    ) {
                                        "а"
                                    } else if (Math.abs(forecast.current.temperature) % 100 / 10 !== 1 && Math.abs(
                                            forecast.current.temperature
                                        ) % 10 === 1
                                    ) {
                                        ""
                                    } else {
                                        "ов"
                                    }
                                return@map java.lang.String.format(
                                    context.getString(R.string.output_whatistheweather),
                                    forecast.current.temperature,
                                    temperatureSuffix,
                                    java.lang.String.join(
                                        ", ",
                                        forecast.current.weatherDescriptions
                                    ).toLowerCase()
                                )
                            } else {
                                val temperatureSuffix: String
                                temperatureSuffix =
                                    if (Math.abs(forecast.current.temperature) === 1) {
                                        ""
                                    } else {
                                        "s"
                                    }
                                return@map java.lang.String.format(
                                    context.getString(R.string.output_whatistheweather),
                                    forecast.current.temperature,
                                    temperatureSuffix,
                                    java.lang.String.join(
                                        ", ",
                                        forecast.current.weatherDescriptions
                                    ).toLowerCase()
                                )
                            }
                        } catch (e: Exception) {
                            throw Exception(context.getString(R.string.output_whatistheweather_incorrect))
                        }
                    }
            }
        answers[context.getString(R.string.input_converttostring)] =
            Function<String, Observable<String>> { input: String ->
                Observable.create(
                    ObservableOnSubscribe { emitter: ObservableEmitter<String> ->
                        val numberPattern =
                            Pattern.compile(
                                context.getString(R.string.input_converttostring) + " (\\d+)",
                                Pattern.CASE_INSENSITIVE
                            )
                        val matcher = numberPattern.matcher(input)
                        if (matcher.find()) {
                            emitter.onNext(matcher.group(1))
                            emitter.onComplete()
                        } else {
                            emitter.onError(Exception(context.getString(R.string.output_converttostring_incorrect)))
                        }
                    }
                )
                    .flatMap(NumberStringService::getNumberString)
                    .map { numberString: NumberString ->
                        try {
                            return@map numberString.numberString
                        } catch (e: Exception) {
                            throw Exception(context.getString(R.string.output_converttostring_incorrect))
                        }
                    }
            }
        answers[context.getString(R.string.input_whataholiday)] =
            Function { input: String ->
                Observable.create { emitter: ObservableEmitter<String> ->
                    try {
                        val dates = getDates(
                            input.substring(context.getString(R.string.input_whataholiday).length)
                                .trim { it <= ' ' }
                        )
                        val result =
                            Arrays.stream(dates)
                                .map<String> { localDate: LocalDate ->
                                    try {
                                        val holidays: String =
                                            ParsingHtmlService.getHoliday(localDate)
                                        return@map String.format(
                                            "%1\$td %1\$tB %1\$tY - %2\$s;",
                                            localDate,
                                            if (holidays.isEmpty()) context.getString(R.string.output_whataholiday_no) else holidays
                                        )
                                    } catch (exception: Exception) {
                                        return@map null
                                    }
                                }
                                .filter { o: String? ->
                                    Objects.nonNull(
                                        o
                                    )
                                }
                                .collect(
                                    Collectors.toList()
                                )
                        if (result.isEmpty()) {
                            throw Exception()
                        } else {
                            emitter.onNext(java.lang.String.join("\n", result))
                        }
                        emitter.onComplete()
                    } catch (e: Exception) {
                        emitter.onError(Exception(context.getString(R.string.output_whataholiday_incorrect)))
                    }
                }
            }

        answers[context.getString(R.string.input_moviesinthetop)] =
            Function { input: String ->
                Observable.create { emitter: ObservableEmitter<String> ->

                    val movies = ParsingHtmlService.getLatestMovies()
                    val moviesText = StringBuilder()
                    if (movies.isEmpty()) {
                        throw Exception()
                    } else {
                        movies.forEach { movie -> moviesText.append("Название: " + movie.name + "; рейтинг: " + movie.rating + "\n")}
                        emitter.onNext(moviesText.toString())
                    }
                    emitter.onComplete()
                }
            }

        answers[context.getString(R.string.input_citygeolocation)] =
            Function<String, Observable<String>> { input: String ->
                Observable.create(
                    ObservableOnSubscribe { emitter: ObservableEmitter<String> ->
                        val city =
                            input.substring(context.getString(R.string.input_citygeolocation).length)
                                .trim { it <= ' ' }
                        if (!city.isEmpty()) {
                            emitter.onNext(city)
                            emitter.onComplete()
                        } else {
                            emitter.onError(Exception(context.getString(R.string.output_whatistheweather_incorrect)))
                        }
                    }
                )
                    .flatMap(GeocodeService::getGeolocation)
                    .map { geolocation: Geolocation ->
                        try {
                            return@map context.getString(R.string.latitude) + ": " + geolocation.data.first().latitude + "\n" + context.getString(R.string.longitude) + ": " + geolocation.data.first().longitude
                        } catch (e: Exception) {
                            throw Exception(context.getString(R.string.output_city_incorrect))
                        }
                    }
            }
    }
    fun subscribe(observer: Observer<String?>?) {
        behaviorSubject!!.subscribe(observer)
    }

    fun getAnswer(input: String) {
        val inp = input.toLowerCase()
        var observable =
            Observable.just(context!!.getString(R.string.output_unknown))
        for (answer in answers.keys) {
            if (inp.contains(answer)) {
                observable = answers[answer]!!.apply(inp)
                break
            }
        }
        observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturn { obj: Throwable -> obj.message }
            .subscribe { t: String? ->
                behaviorSubject!!.onNext(t)
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Throws(DateTimeParseException::class)
    private fun getDates(input: String): Array<LocalDate> {
        val dates = input.split(", ".toRegex()).toTypedArray()
        val result = ArrayList<LocalDate>()
        for (date in dates) {
            try {
                if (date == context.getString(R.string.today)) {
                    result.add(LocalDate.now())
                } else if (date == context.getString(R.string.yesterday)) {
                    result.add(LocalDate.now().minusDays(1))
                } else if (date == context.getString(R.string.tomorrow)) {
                    result.add(LocalDate.now().plusDays(1))
                } else if (date.matches(Regex("((\\d){1,2}\\.){2}(\\d){4}"))) {
                    result.add(
                        LocalDate.parse(
                            date,
                            DateTimeFormatter.ofPattern("d.MM.yyyy")
                        )
                    )
                } else if (date.matches(Regex("((\\d){1,2} ){2}(\\d){4}"))) {
                    result.add(
                        LocalDate.parse(
                            date,
                            DateTimeFormatter.ofPattern("d MM yyyy")
                        )
                    )
                } else {
                    result.add(
                        LocalDate.parse(
                            date,
                            DateTimeFormatter.ofPattern(
                                "d MMMM yyyy",
                                Locale.getDefault()
                            )
                        )
                    )
                }
            } catch (ignored: Exception) {
            }
        }
        return result.toTypedArray();
    }
}