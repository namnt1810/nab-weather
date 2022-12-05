package vn.namnt.nabweather.data.remote

import vn.namnt.nabweather.common.TemperatureUnit.METRIC
import vn.namnt.nabweather.entity.WeatherInfo

/**
 * @author namnt
 * @since 02/12/2022
 */
internal object MockData {
    val remoteWeatherInfo: List<WeatherInfo> = listOf(
        WeatherInfo(1669953600, 30.08f, METRIC, 1008, 59, "light rain"),
        WeatherInfo(1670040000, 27.75f, METRIC, 1009, 69, "light rain"),
        WeatherInfo(1670126400, 25.63f, METRIC, 1011, 78, "light rain"),
        WeatherInfo(1670212800, 29.45f, METRIC, 1011, 67, "light rain"),
        WeatherInfo(1670299200, 29.21f, METRIC, 1010, 68, "light rain"),
        WeatherInfo(1670385600, 28.64f, METRIC, 1010, 70, "light rain"),
        WeatherInfo(1670472000, 29.38f, METRIC, 1010, 66, "light rain"),
    )

    val remoteResponseJson = """
        {
            "city": {
                "id": 1580578,
                "name": "Ho Chi Minh City",
                "coord": {
                    "lon": 106.6667,
                    "lat": 10.8333
                },
                "country": "VN",
                "population": 0,
                "timezone": 25200
            },
            "cod": "200",
            "message": 14.12541,
            "cnt": 7,
            "list": [
                {
                    "dt": 1669953600,
                    "sunrise": 1669935406,
                    "sunset": 1669976926,
                    "temp": {
                        "day": 30.08,
                        "min": 23.69,
                        "max": 31.97,
                        "night": 24.61,
                        "eve": 31.21,
                        "morn": 24.27
                    },
                    "feels_like": {
                        "day": 32.78,
                        "night": 25.14,
                        "eve": 34.79,
                        "morn": 25.02
                    },
                    "pressure": 1008,
                    "humidity": 59,
                    "weather": [
                        {
                            "id": 500,
                            "main": "Rain",
                            "description": "light rain",
                            "icon": "10d"
                        }
                    ],
                    "speed": 5.68,
                    "deg": 108,
                    "gust": 12.8,
                    "clouds": 96,
                    "pop": 0.61,
                    "rain": 1.21
                },
                {
                    "dt": 1670040000,
                    "sunrise": 1670021836,
                    "sunset": 1670063342,
                    "temp": {
                        "day": 27.75,
                        "min": 23.68,
                        "max": 27.75,
                        "night": 23.71,
                        "eve": 25.06,
                        "morn": 24.06
                    },
                    "feels_like": {
                        "day": 30.07,
                        "night": 24.46,
                        "eve": 25.81,
                        "morn": 24.69
                    },
                    "pressure": 1009,
                    "humidity": 69,
                    "weather": [
                        {
                            "id": 500,
                            "main": "Rain",
                            "description": "light rain",
                            "icon": "10d"
                        }
                    ],
                    "speed": 2.7,
                    "deg": 170,
                    "gust": 6.66,
                    "clouds": 99,
                    "pop": 0.9,
                    "rain": 1.17
                },
                {
                    "dt": 1670126400,
                    "sunrise": 1670108267,
                    "sunset": 1670149758,
                    "temp": {
                        "day": 25.63,
                        "min": 23.05,
                        "max": 30.27,
                        "night": 24.71,
                        "eve": 29.22,
                        "morn": 23.19
                    },
                    "feels_like": {
                        "day": 26.29,
                        "night": 25.48,
                        "eve": 32.83,
                        "morn": 23.99
                    },
                    "pressure": 1011,
                    "humidity": 78,
                    "weather": [
                        {
                            "id": 500,
                            "main": "Rain",
                            "description": "light rain",
                            "icon": "10d"
                        }
                    ],
                    "speed": 2.12,
                    "deg": 14,
                    "gust": 3.65,
                    "clouds": 100,
                    "pop": 0.68,
                    "rain": 1.2
                },
                {
                    "dt": 1670212800,
                    "sunrise": 1670194698,
                    "sunset": 1670236175,
                    "temp": {
                        "day": 29.45,
                        "min": 23.58,
                        "max": 31.51,
                        "night": 25.02,
                        "eve": 29.12,
                        "morn": 23.58
                    },
                    "feels_like": {
                        "day": 33.12,
                        "night": 25.77,
                        "eve": 32.8,
                        "morn": 24.34
                    },
                    "pressure": 1011,
                    "humidity": 67,
                    "weather": [
                        {
                            "id": 500,
                            "main": "Rain",
                            "description": "light rain",
                            "icon": "10d"
                        }
                    ],
                    "speed": 2.22,
                    "deg": 69,
                    "gust": 4.5,
                    "clouds": 82,
                    "pop": 0.6,
                    "rain": 1.19
                },
                {
                    "dt": 1670299200,
                    "sunrise": 1670281129,
                    "sunset": 1670322593,
                    "temp": {
                        "day": 29.21,
                        "min": 23.67,
                        "max": 31.42,
                        "night": 25.3,
                        "eve": 30.7,
                        "morn": 23.67
                    },
                    "feels_like": {
                        "day": 32.8,
                        "night": 26.08,
                        "eve": 34.22,
                        "morn": 24.47
                    },
                    "pressure": 1010,
                    "humidity": 68,
                    "weather": [
                        {
                            "id": 500,
                            "main": "Rain",
                            "description": "light rain",
                            "icon": "10d"
                        }
                    ],
                    "speed": 2.62,
                    "deg": 171,
                    "gust": 4.45,
                    "clouds": 42,
                    "pop": 0.68,
                    "rain": 1.27
                },
                {
                    "dt": 1670385600,
                    "sunrise": 1670367560,
                    "sunset": 1670409012,
                    "temp": {
                        "day": 28.64,
                        "min": 23.65,
                        "max": 30.97,
                        "night": 24.28,
                        "eve": 27.19,
                        "morn": 23.65
                    },
                    "feels_like": {
                        "day": 31.96,
                        "night": 25.06,
                        "eve": 30.17,
                        "morn": 24.45
                    },
                    "pressure": 1010,
                    "humidity": 70,
                    "weather": [
                        {
                            "id": 500,
                            "main": "Rain",
                            "description": "light rain",
                            "icon": "10d"
                        }
                    ],
                    "speed": 2.43,
                    "deg": 143,
                    "gust": 4.8,
                    "clouds": 90,
                    "pop": 0.7,
                    "rain": 3.86
                },
                {
                    "dt": 1670472000,
                    "sunrise": 1670453992,
                    "sunset": 1670495431,
                    "temp": {
                        "day": 29.38,
                        "min": 23.39,
                        "max": 31.93,
                        "night": 25.27,
                        "eve": 31.22,
                        "morn": 23.39
                    },
                    "feels_like": {
                        "day": 32.78,
                        "night": 26.05,
                        "eve": 34.81,
                        "morn": 24.16
                    },
                    "pressure": 1010,
                    "humidity": 66,
                    "weather": [
                        {
                            "id": 500,
                            "main": "Rain",
                            "description": "light rain",
                            "icon": "10d"
                        }
                    ],
                    "speed": 2.72,
                    "deg": 157,
                    "gust": 5.09,
                    "clouds": 87,
                    "pop": 0.34,
                    "rain": 0.23
                }
            ]

        }
    """.trimIndent()
}