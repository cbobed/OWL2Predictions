# original example from Digg Data website (Takashi J. OZAKI, Ph. D.) 
# http://diggdata.in/post/58333540883/k-fold-cross-validation-in-r
# This code developed using the code;
# https://gist.github.com/ankitksharma/6683552bbb8898894a09#file-k-foldcv-r
# Please star developers to encourage them in their works.

# This code is run over;
# R version 3.2.3 (2015-12-10) -- "Wooden Christmas-Tree"
# Copyright (C) 2015 The R Foundation for Statistical Computing
# Platform: x86_64-w64-mingw32/x64 (64-bit)
options( java.parameters = "-Xmx6g" )

getwd()
setwd("C:/tmpMetrics/data") #change it to point the base dir
getwd()

library(plyr)                    # Progress bar
library(randomForest)
library(XLConnect)               # load XLConnect package 

wk = loadWorkbook("LatestData.xlsx") 
data = readWorksheet(wk, sheet="DataJustTBox")

nrow(data) # 5878

k = 10 #Folds

# sample from 1 to k, nrow times (the number of observations in the data)
data$id <- sample(1:k, nrow(data), replace = TRUE)
list <- 1:k  #  1  2  3  4  5  6  7  8  9 10

prediction <- data.frame()
testsetCopy <- data.frame()

progress.bar <- create_progress_bar("text")
progress.bar$init(k)

for (i in 1:k){
  # remove rows with id i from dataframe to create training set
  # select rows with id i to create test set
  trainingset <- subset(data, id %in% list[-i])
  testset <- subset(data, id %in% c(i))
  
  # run a random forest model
  mymodel <- randomForest(NanoSecs ~ ., data = trainingset, ntree = 1501)  #, ntree = 100 , mtry=124
  
  # remove response column 1, Sepal.Length
  temp <- as.data.frame(predict(mymodel, testset[,-1]))
  # append this iteration's predictions to the end of the prediction data frame
  prediction <- rbind(prediction, temp)
  
  # append this iteration's test set to the test set copy data frame
  # keep only the Sepal Length Column
  testsetCopy <- rbind(testsetCopy, as.data.frame(testset[,1]))
  
  progress.bar$step()
}

# add predictions and actual Sepal Length values
result <- cbind(prediction, testsetCopy[, 1])
names(result) <- c("Predicted", "Actual")
result$Difference <- abs(result$Actual - result$Predicted)

# As an example use Mean Absolute Error as Evalution 
summary(result$Difference)
# Min.  1st Qu.   Median     Mean  3rd Qu.     Max. 
# 0.00002  0.06133  0.14400  1.57200  0.62830 95.29000 

# Export results to see;
# WHAT IS PREDICTED IN 10-FOLD CROS-VALIDATED RANDOM-FOREST REGRESSION
# And what is the ACCURACY ?
library(xlsx)
write.xlsx(result, "Results-DataCombined-1500.xlsx")
detach("package:xlsx", TRUE)

