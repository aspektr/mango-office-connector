# Mongo office connector
get call statistics from api

## Build
`sbt assembly`

## Usage 
`java -jar mongocon.jar --help`

output:
```
mangocon 0.1
Usage: java -jar mongocon.jar [options]

  -k, --vpbx_api_key <value>
                           mandatory vpbx api key
  -s, --vpbx_api_salt <value>
                           mandatory vpbx api salt
  -h, --crpt_hash <value>  optional SHA-256 by default
  -c, --char_set <value>   optional UTF-8 by default
  -r, --uri_request_report <value>
                           optional https://app.mango-office.ru/vpbx/stats/request by default
  -n, --uri_result_stats <value>
                           optional https://app.mango-office.ru/vpbx/result/stats by default
  -r, --uri_stats_result <value>
                           optional https://app.mango-office.ru/vpbx/result/stats by default
  -f, --from <value>       mandatory accepts a value like --from 2000-12-01
  -t, --to <value>         mandatory accepts a value like --to 2000-12-01
  --help                   print help message
```

### example of usage from command promt
`java -jar mongocon.jar -k $key -s #salt -f 2020-10-13 -t 2020-10-14` 

Results will be saved in `current_dir\out.csv`

### example of usage from code

```scala
import java.util.Calendar
import java.text.SimpleDateFormat
import ru.azurdrive.MangoConnector.fetchStats

val key = "key"
val salt = "salt"

val fmt = new SimpleDateFormat("dd-MM-yyyy")
val from = Calendar.getInstance()
val to = Calendar.getInstance()

from.setTime(fmt.parse("13-10-2020"))
to.setTime(fmt.parse("14-10-2020"))
val stats: List[String] = fetchStats(key, salt, from, to)
```




