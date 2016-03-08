# Streaming Tweet Processing

### Andrea Soto

## Overview

System for collecting data about 'popular' hashtags and users related to the tweets containing them. 

The popularity of hashtags is determined by the frequency of occurrence of those hashtags in tweets over a sampling period. The system also tracks the authoring user of the tweet containing the hashtag and any user(s) mentioned. For example, if @solange tweets "@jayZ is performing #theblackalbum tonight at Madison Square Garden!! @beyonce will be there!", 'theblackalbum' is a popular topic, and all of the users related to the tweet—'beyonce', 'solange', and 'jayZ'— are recorded.

# File Structure and Execution

The file structure of the program is as follows:

```bash
./twitter_popularity
├── build.sbt
├── project
│   └── plugins.sbt
└── twitter_popularity.scala
```

The program is executed by running the following line the command line.

```bash
$SPARK_HOME/bin/spark-submit --master spark://spark1:7077 \
$(find target -iname "*assembly*.jar") <Sampling_Interval> <Aggregation_Interval> <TopN> <Optional_Filter>
````

