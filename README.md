# Streaming Tweet Processing

### Andrea Soto

## Overview

System for collecting data about 'popular' hashtags and users related to the tweets containing them. 

The popularity of hashtags is determined by the frequency of occurrence of those hashtags in tweets over a sampling period. The system also tracks the authoring user of the tweet containing the hashtag and any user(s) mentioned. For example, if @solange tweets "@jayZ is performing #theblackalbum tonight at Madison Square Garden!! @beyonce will be there!", 'theblackalbum' is a popular topic, and all of the users related to the tweet—'beyonce', 'solange', and 'jayZ'— are recorded.

## File Structure and Execution

The file structure of the program is as follows:

```bash
./twitter_popularity
├── build.sbt
├── project
│   └── plugins.sbt
└── twitter_popularity.scala
```

The program is executed by running the following in the command line.

```bash
$SPARK_HOME/bin/spark-submit --master spark://spark1:7077 \
$(find target -iname "*assembly*.jar") <Sampling_Interval> <Aggregation_Interval> <TopN> <Optional_Filter>
````

Where the following are input parameters:

- **Sampling_Interval (sec):** Interval to sample tweets
- **Aggregation_Interval (sec):** How much time should be used to aggregate and count popular tweets. 
- **TopN:** Number of top hashtags to report
- **Optional_Filter (optional):** Terms to filter tweets (if more than one, separate each term with a space)

## Output

A sample of the output is shown below:
A sample of the output is shown below:

```bash
20:47:20
Top 10 hashtags in last 10 seconds (55 total):
  #KCA (6 tweets)
     Users (5):    Ale_Cazaress,KingTommoStyles,1D_harloulini,LashanahBenally,OliviaXAM
     Mentions (10): 1DHQDaily,NickelodeonUK,onedirection,NickelodeonUK,onedirection,NickelodeonUK,onedirection,1DHQDaily,NickelodeonUK,onedirection
  #VoteDragMeDownUK (5 tweets)
     Users (5):    Ale_Cazaress,KingTommoStyles,1D_harloulini,LashanahBenally,OliviaXAM
     Mentions (8): NickelodeonUK,onedirection,NickelodeonUK,onedirection,1DHQDaily,NickelodeonUK,onedirection,1DHQDaily
  #music (3 tweets)
     Users (3):    LABEL_MARS_BOT,GeorgiaLoverUSA,Asianbunny420
     Mentions (2): TheIdesofJune,BuyOnBudget
```

The program first prints the time of the report, followed by a line summarizing the report (number of hasthags, aggregation interval, and total tweets seen in the sample)

Then, the hashtags are printed in order of importance (most popular to least popular). For each hashtag, the corresponding users and mentions are printed immediately after the hashtag.

## Notes of Interest

### Filtering

The first run was done without filters, and the results proved to be of little significance (file *result_noFilter.txt*).  Because no filters were used, the most popular hashtags were largely unrelated to each other. For example, the top-10 would include #KCA, #VoteEnriqueFPP, #DemDebate, #HoróscopoZino, and #EvilRegals, which are unrelated hashtags and would provide little marketing insights.

So I included tweet filters so that the hashtags would be related to a category that marketers were targeting. I used the term ‘music’ in the remaining runs.

### Changing Intervals

Using the filter ‘music’, the program was run several times with different sampling and aggregations intervals. The following three configurations were tested:

-	10 sec interval, 10 sec aggregation (results_1.txt)
-	5 sec interval, 15 sec aggregation (results_2.txt)
-	5 sec interval, 30 sec aggregation (results_3.txt)

The results showed that longer aggregation intervals gave more insight into the most popular hashtags and of gradual changes in popularity. With shorter intervals, hashtags that were temporarily oversampled would jump to the top and then quickly disappear. This was not the case with longer windows.

### KCA

Using the term ‘music’ to filter tweets resulted in a clear winner: #KCA. KCA stands for the **Nickelodeon Kids' Choice Awards** which takes place in March 12, 2016. Other popular tweets were related to the KCA. For example, #Vote1DirectionUK and #VoteDragMeDownUK were very popular, and are hashtags encouraging fans to vote for the song “Drag Me Down” of One Direction which are nominated for the KCA.
