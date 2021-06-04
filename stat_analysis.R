
#install.packages("car")
#install.packages("readr")
#install.packages("glmulti")
#install.packages("rJava")
#install.packages("lmtest")
#install.packages("cowplot")
library(ggplot2)
library(car)
library(readr)
library(glmulti)
library(lmtest)
library(dplyr)

#previous input file versions
#03082019_location_appended_input_R_v2.csv
#08282019_input_R_v4
#input_R_v4_new_15_11_2019.csv
#11152019_location_appended_input_R_v4_new_15_11_2019.csv

#input file path 
input_R_v2 <- read_csv("/Users/le/Documents/GitHub/CodeSwitchingResearch/11152019_two_entropies_appended_input_R_v4.csv", 
                       col_types = cols(sent_type = col_factor(levels = c("non-code-switch", "code-switch")),
                                        sent_id = col_integer(), 
                                        university = col_factor(levels = c("CMU","PIT", "PSU")), 
                                        
                                        #Categorical
                                        #location encodings
                                        `10_80_10_percent_location` = col_factor(levels = c("1", "2", "3")), 
                                        `25_50_25_percent_location` = col_factor(levels = c("1", "2", "3")), 
                                        `30_40_30_percent_location` = col_factor(levels = c("1", "2", "3")), 
                                        first_middle_last_location = col_factor(levels = c("1", "2", "3")), 
                                        
                                        #pos_tag_first_cs_word_trans=col_factor(),#introduced later
                                        #deprel_first_cs_word_trans=col_factor(),#introduced later
                                        
                                        #Boolean
                                        if_it_is_root = col_logical(),  
                                        if_previous_word_is_punctuation = col_logical(),
                                        
                                        #Numerical
                                        word_id = col_integer(),
                                        frequency_negative_ln_first_cs_word_trans = col_double(),
                                        surprisal_first_cs_word_trans=col_double(),
                                        average_surprisal = col_double(),
                                        surprisal_of_previous_word=col_double(),
                                        #frequencies obtained with OUR corpus only, we don't report results with them because of the limited size of our corpus, but they seem interesting
                                        bilingual_corpus_frequency_negative_log_first_cs_word=col_double(),
                                        bilingual_corpus_frequency_negative_log_first_cs_word_trans=col_double(),
                                        
                                        entropy_at_cs_point = col_double(),
                                        entropy_one_word_after_cs_point= col_double(),
                                        
                                        length_first_cs_word_form = col_integer(), 
                                        length_first_cs_word_trans = col_integer(), 
                                        dependency_distance = col_integer(), 
                                        translation_sentence_length = col_integer()
                       )
                       , trim_ws = FALSE)


View(input_R_v2)
head(input_R_v2)
str(input_R_v2)

#count rows of data frame
nrow(input_R_v2)
nrow(input_R_v2[which(input_R_v2$sent_type=="code-switch"),])
nrow(input_R_v2[which(input_R_v2$sent_type=="non-code-switch"),])

plot(input_R_v2$sent_type,input_R_v2$length_first_cs_word_trans)
plot(input_R_v2$sent_type,input_R_v2$translation_sentence_length)
plot(input_R_v2$sent_type,input_R_v2$dependency_distance)
plot(input_R_v2$sent_type,input_R_v2$surprisal_first_cs_word_trans)
plot(input_R_v2$sent_type,input_R_v2$average_surprisal)
plot(input_R_v2$sent_type,input_R_v2$surprisal_of_previous_word)
plot(input_R_v2$sent_type,input_R_v2$entropy_at_cs_point)
plot(input_R_v2$sent_type,input_R_v2$entropy_one_word_after_cs_point)
plot(input_R_v2$sent_type,input_R_v2$frequency_negative_ln_first_cs_word_trans)
plot(input_R_v2$sent_type,input_R_v2$bilingual_corpus_frequency_negative_log_first_cs_word_trans)

##############################
### PREPROCESSING
##############################

###POSTAGS###
#Get Unique postags
unique(input_R_v2$pos_tag_first_cs_word_trans)
#https://www.sketchengine.eu/chinese-penn-treebank-part-of-speech-tagset/
#convert all postags that are NR (proper noun), NN (common noun) or NT (temporal noun) to "Noun"
input_R_v2<- input_R_v2 %>%
  mutate(postag_converted = case_when(input_R_v2$pos_tag_first_cs_word_trans == 'NR' ~ 'Noun',
                                      input_R_v2$pos_tag_first_cs_word_trans == 'NN' ~ 'Noun',
                                      input_R_v2$pos_tag_first_cs_word_trans == 'NT' ~ 'Noun',
                                      input_R_v2$pos_tag_first_cs_word_trans == 'VE' ~ 'Verb',
                                      input_R_v2$pos_tag_first_cs_word_trans == 'VV' ~ 'Verb',
                                      TRUE ~ 'Other'))
myvars <- c("sent_type", "pos_tag_first_cs_word_trans", "postag_converted")
new_view<-input_R_v2[myvars]
View(new_view)

#review levels
input_R_v2$postag_converted<- as.factor(input_R_v2$postag_converted)
levels(input_R_v2$postag_converted)
levels(input_R_v2$postag_converted) <- c("Noun","Verb", "Other")

#Plot the distribution of postags
ggplot(input_R_v2, aes(pos_tag_first_cs_word_trans, ..count..)) + geom_bar(aes(fill = sent_type), position = "dodge")
ggplot(input_R_v2, aes(postag_converted, ..count..)) + geom_bar(aes(fill = sent_type), position = "dodge")
nrow(input_R_v2[which(input_R_v2$postag_converted=="Noun"),])
nrow(input_R_v2[which(input_R_v2$postag_converted=="Verb"),])
nrow(input_R_v2[which(input_R_v2$postag_converted=="Other"),])


###DEPENDENCY RELATIONS###
#Get unique dependencies
unique(input_R_v2$deprel_first_cs_word_trans)
ggplot(input_R_v2, aes(deprel_first_cs_word_trans, ..count..)) + geom_bar(aes(fill = sent_type), position = "dodge")
summary(input_R_v2$deprel_first_cs_word_trans)

#Remove the dependencies with less than 100 observations
input_R_v2$deprel_first_cs_word_trans[which(input_R_v2$deprel_first_cs_word_trans!='compound:nn' & input_R_v2$deprel_first_cs_word_trans!='nsubj' & input_R_v2$deprel_first_cs_word_trans!='dobj'  & input_R_v2$deprel_first_cs_word_trans!='root' &  input_R_v2$deprel_first_cs_word_trans!='dep' & input_R_v2$deprel_first_cs_word_trans!='amod')] <- "Other" 
input_R_v2$deprel_first_cs_word_trans<- as.factor(input_R_v2$deprel_first_cs_word_trans)
levels(input_R_v2$deprel_first_cs_word_trans)<-c("compound:nn","nsubj","dobj","root","dep","amod","Other")
ggplot(input_R_v2, aes(deprel_first_cs_word_trans, ..count..)) + geom_bar(aes(fill = sent_type), position = "dodge")

###DEPENDENCY DISTANCE###
#only consider dep distance if the governor is to the left of the cs-point, 0 otherwise
input_R_v2$dependency_distance_left <- ifelse(input_R_v2$word_id > input_R_v2$governor_first_cs_word_trans & input_R_v2$governor_first_cs_word_trans>0 , input_R_v2$word_id - input_R_v2$governor_first_cs_word_trans, 0)
myvars_dep<- c("sent_type", "word_id", "governor_first_cs_word_trans","dependency_distance","dependency_distance_left")
new_view2<-input_R_v2[myvars_dep]
View(new_view2)
plot(input_R_v2$sent_type,input_R_v2$dependency_distance_left)

###IF WORD IS AT THE BEGINNING, PREVIOUS_WORD_IS_PUNCTUATION IS CONSIDERED TRUE
input_R_v2$if_previous_word_is_punctuation [is.na(input_R_v2$if_previous_word_is_punctuation)] <- TRUE

#DEVIATION FROM MEAN SURPRISAL #NOT USED
input_R_v2$deviation_from_mean_surprisal <-abs(input_R_v2$average_surprisal-input_R_v2$surprisal_first_cs_word_trans)
myvars <- c("surprisal_first_cs_word_trans", "average_surprisal", "deviation_from_mean_surprisal")
new_view<-input_R_v2[myvars]
View(new_view)

#DEVIATION FROM LAST WORD'S SURPRISAL #NOT USED
input_R_v2$deviation_from_lastword_surprisal <-abs(input_R_v2$surprisal_of_previous_word-input_R_v2$surprisal_first_cs_word_trans)
myvars <- c("surprisal_first_cs_word_trans", "surprisal_of_previous_word", "surprisal_first_cs_word_trans")
new_view<-input_R_v2[myvars]
View(new_view)

#There shouldn't be any NAs in the frequencies
nrow(input_R_v2)
nrow(input_R_v2[!(is.na(input_R_v2$frequency_negative_ln_first_cs_word_trans)),])

#SENTENCE LENGTHS 
h <-hist(input_R_v2$translation_sentence_length, xlim = c(0,40), breaks=50)
text(h$mids,h$counts,labels=h$counts, adj=c(0.5, -0.5))
axis(side=1, at=seq(0,43, 1), labels=seq(0,43,1))
h
min(input_R_v2$translation_sentence_length)#2
max(input_R_v2$translation_sentence_length)#43
mean(input_R_v2$translation_sentence_length)#11.09

#CS-POINTS
cspoint_h<-hist(input_R_v2$word_id, xlim = c(0,40), breaks=30)
text(cspoint_h$mids,cspoint_h$counts,labels=cspoint_h$counts, adj=c(0.5, -0.5))
axis(side=1, at=seq(0,30, 1), labels=seq(0,30,1))
max(input_R_v2$word_id)#30
min(input_R_v2$word_id)#1
mean(input_R_v2$word_id)#5.154472

lambda= 1/mean(input_R_v2$word_id)# 0.194
std.dv=sqrt(1/lambda^2)
range=seq(0,5.154472 + 5*std.dv,0.01)
y=dexp(range,lambda)
plot(range,y,type="l", ylim=c(0,max(y)+0.01))


######################
###STANDARDIZATION###
######################
#not used:
input_R_v2$average_surprisal<-scale(input_R_v2$average_surprisal,center = TRUE, scale=TRUE) #not significant
#not defined for sentence-initial CSs:
input_R_v2$surprisal_of_previous_word<-scale(input_R_v2$surprisal_of_previous_word,center=TRUE, scale=TRUE) #not significant
input_R_v2$deviation_from_lastword_surprisal<-scale(input_R_v2$deviation_from_lastword_surprisal,center = TRUE, scale = TRUE) #not significant... plus the notion of surprisal is related to the last word; and this is not defined for initial words

input_R_v2$bilingual_corpus_frequency_negative_log_first_cs_word_trans<-scale(input_R_v2$bilingual_corpus_frequency_negative_log_first_cs_word_trans,center=TRUE,scale=TRUE)
input_R_v2$bilingual_corpus_frequency_negative_log_first_cs_word_trans<-input_R_v2$bilingual_corpus_frequency_negative_log_first_cs_word_trans * 0.5

#used
input_R_v2$frequency_negative_ln_first_cs_word_trans <- scale(input_R_v2$frequency_negative_ln_first_cs_word_trans, center=TRUE, scale=TRUE)
input_R_v2$frequency_negative_ln_first_cs_word_trans <-input_R_v2$frequency_negative_ln_first_cs_word_trans *0.5
input_R_v2$surprisal_first_cs_word_trans <- scale(input_R_v2$surprisal_first_cs_word_trans, center=TRUE, scale=TRUE)
input_R_v2$surprisal_first_cs_word_trans <-input_R_v2$surprisal_first_cs_word_trans *0.5
input_R_v2$deviation_from_mean_surprisal<-scale(input_R_v2$deviation_from_mean_surprisal,center = TRUE, scale = TRUE)
input_R_v2$deviation_from_mean_surprisal<-input_R_v2$deviation_from_mean_surprisal *0.5

input_R_v2$entropy_at_cs_point<-scale(input_R_v2$entropy_at_cs_point,center = TRUE, scale = TRUE)
input_R_v2$entropy_at_cs_point<-input_R_v2$entropy_at_cs_point* 0.5
input_R_v2$entropy_one_word_after_cs_point<-scale(input_R_v2$entropy_one_word_after_cs_point,center = TRUE, scale = TRUE)
input_R_v2$entropy_one_word_after_cs_point<-input_R_v2$entropy_one_word_after_cs_point*0.5

input_R_v2$length_first_cs_word_trans <-scale(input_R_v2$length_first_cs_word_trans, center = TRUE, scale = TRUE)
input_R_v2$length_first_cs_word_trans <-input_R_v2$length_first_cs_word_trans *0.5

input_R_v2$dependency_distance <-scale(input_R_v2$dependency_distance, center = TRUE, scale = TRUE)
input_R_v2$dependency_distance <-input_R_v2$dependency_distance *0.5
input_R_v2$dependency_distance_left <-scale(input_R_v2$dependency_distance_left, center = TRUE, scale = TRUE)
input_R_v2$dependency_distance_left <-input_R_v2$dependency_distance_left *0.5


input_R_v2$translation_sentence_length <- scale(input_R_v2$translation_sentence_length, center = TRUE, scale=TRUE)
input_R_v2$translation_sentence_length <-input_R_v2$translation_sentence_length *0.5

###########################################
####SOME MODELLING WITH SINGLE VARIABLES####
###########################################

#############SURPRISALS  
#Surprisal -->SIGNIFICANT
m_surp <- glm(sent_type ~ surprisal_first_cs_word_trans, data=input_R_v2, family="binomial")
summary(m_surp)
#Mean surprisal -->NOT significant
m_aversurp <- glm(sent_type ~ average_surprisal, data=input_R_v2, family="binomial")
summary(m_aversurp)
#Deviation from mean surprisal -- >SIGNIFICANT
m_differ_aversurp <- glm(sent_type ~ deviation_from_mean_surprisal, data=input_R_v2, family="binomial")
summary(m_differ_aversurp) #significant 0.2718427  0.0747359   3.637 0.000275 ***
#Last words's surprisal --> NOT significant
m_las_word <- glm(sent_type ~surprisal_of_previous_word, data=input_R_v2, family="binomial")
summary(m_las_word) 
#Deviation from last words's surprisal -->SIGNIFICANT  0.11203    0.04075   2.749  0.00598 **
m_lastword_deviation<- glm(sent_type ~deviation_from_lastword_surprisal, data=input_R_v2, family="binomial")
summary(m_lastword_deviation) 

#Frequency -->SIGNIFICANT
m_freq <- glm(sent_type ~ frequency_negative_ln_first_cs_word_trans, data=input_R_v2, family="binomial")
summary(m_freq)
#Local Frequencey (from our corpus) -->SIGNIFICANT
m_local_freq<- glm(sent_type ~ bilingual_corpus_frequency_negative_log_first_cs_word_trans, data=input_R_v2, family="binomial")
summary(m_local_freq)


############SURPRISAL PLUS ... 
#Mean Surprisal--> NOT significant 
m_aversurp_surp <- glm(sent_type ~ surprisal_first_cs_word_trans + average_surprisal, data=input_R_v2, family="binomial")
summary(m_aversurp_surp)
#Deviation from Mean surprisal -->NOT significant
m_differ_aversurp_surp <- glm(sent_type ~ surprisal_first_cs_word_trans + deviation_from_mean_surprisal, data=input_R_v2, family="binomial")
summary(m_differ_aversurp_surp)
#Last words's surprisal --> NOT significant
m_las_word_surp <- glm(sent_type ~ surprisal_first_cs_word_trans + surprisal_of_previous_word, data=input_R_v2, family="binomial")
summary(m_las_word_surp)
#Deviation from Last words's surprisal --> NOT significant
m_lastword_deviation_surp<- glm(sent_type ~ surprisal_first_cs_word_trans + deviation_from_lastword_surprisal, data=input_R_v2, family="binomial")
summary(m_lastword_deviation_surp)
##It seems that combinations of surprisal plus some other related measure of surprisal results in models where only surprisal is significant

#Frequency -->SIGNIFICANT
m_surp_freq <- glm(sent_type ~ surprisal_first_cs_word_trans + frequency_negative_ln_first_cs_word_trans, data=input_R_v2, family="binomial")
summary(m_surp_freq)
#Local Frequency -->SIGNIFICANT
m_local_freq_surp<- glm(sent_type ~ surprisal_first_cs_word_trans + bilingual_corpus_frequency_negative_log_first_cs_word_trans, data=input_R_v2, family="binomial")
summary(m_local_freq_surp)#Having the local surprisal seems to improve things rather than the wikipedia surprisal
#Frequency AND Local Frequency --> ONLY Local Frequency Significant
m_localfreq_surp_freq<- glm(sent_type ~ frequency_negative_ln_first_cs_word_trans+ surprisal_first_cs_word_trans + bilingual_corpus_frequency_negative_log_first_cs_word_trans, data=input_R_v2, family="binomial")
summary(m_localfreq_surp_freq)#Having the local frequency seems to be better than the google 1-gram - ?


##########ENTROPIES
#Entropy -->NOT significant
m_ent <- glm(sent_type ~ entropy_at_cs_point, data=input_R_v2, family="binomial")
summary(m_ent)
#Entropy at one word after CS --> NOT significant
m_ent_plus1 <- glm(sent_type ~  entropy_one_word_after_cs_point, data = input_R_v2, family = "binomial")
summary(m_ent_plus1)

#Entropy plus surprisal --> only surprisal significant
m_ent_surp <- glm(sent_type ~ surprisal_first_cs_word_trans+ entropy_at_cs_point, data=input_R_v2, family="binomial")
summary(m_ent_surp)
#EntropyP1 and Surprisal --> only surprisal significant
m_ent_plus1 <- glm(sent_type ~ surprisal_first_cs_word_trans + entropy_one_word_after_cs_point, data = input_R_v2, family = "binomial")
summary(m_ent_plus1)

###POSTAGS
#Postag -->not significant
m_pos <- glm(sent_type ~ postag_converted, data=input_R_v2, family = "binomial")
summary(m_pos)
plot(input_R_v2$postag_converted,input_R_v2$surprisal_first_cs_word_trans )
Anova(m_pos)

#Surprisal and POS --> pos not significant
m_surp_pos <- glm(sent_type ~ surprisal_first_cs_word_trans + postag_converted, data=input_R_v2, family = "binomial")
model.matrix(input_R_v2$sent_type ~ input_R_v2$surprisal_first_cs_word_trans + input_R_v2$postag_converted)
summary(m_surp_pos)

#Surprisal, Frequency and POS with interaction --> some significance of surprisal:other
m_surp_x_pos <- glm(sent_type ~ surprisal_first_cs_word_trans * postag_converted + frequency_negative_ln_first_cs_word_trans*postag_converted, data=input_R_v2, family = "binomial")
summary(m_surp_x_pos)

#Word Length of the translation-->significant
m_wordlength <- glm(sent_type ~ length_first_cs_word_trans, data=input_R_v2, family = "binomial")
summary(m_wordlength)


######DEPENDENCIES
#Whether the CS_word is the root of the sentence -->not significant
m_root <- glm(sent_type ~ if_it_is_root, data=input_R_v2, family = "binomial")
summary(m_root)
View(input_R_v2$if_it_is_root)
View(input_R_v2$deprel_first_cs_word_trans)

#Dependency distance -->not significant
plot(input_R_v2$sent_type,input_R_v2$dependency_distance)
m_depdistance <- glm(sent_type ~ dependency_distance, data=input_R_v2, family = "binomial")
summary(m_depdistance)

#Dependency distance, only if governor to the left --> not significant
plot(input_R_v2$sent_type,input_R_v2$dependency_distance_left)
m_depdistance_left <- glm(sent_type ~ dependency_distance_left, data=input_R_v2, family = "binomial")
summary(m_depdistance_left)


#Sentence Length SIGNIFICANT, ALSO WITH SURPRISAL
plot(input_R_v2$sent_type,input_R_v2$translation_sentence_length)
m_sentlen <-glm(sent_type~ translation_sentence_length, data=input_R_v2, family = "binomial")
summary(m_sentlen)
m_sentlen_surp <-glm(sent_type~ surprisal_first_cs_word_trans + translation_sentence_length, data=input_R_v2, family = "binomial")
summary(m_sentlen_surp)

##########LOCATION#############
m_location30_40_30 <- glm(sent_type ~ `30_40_30_percent_location` , data=input_R_v2, family = "binomial")
summary(m_location30_40_30)#AIC:4088
m_location25_50_25 <- glm(sent_type ~ `25_50_25_percent_location` , data=input_R_v2, family = "binomial")
summary(m_location25_50_25)#AIC:4087
m_locationfirst_middle_last <- glm(sent_type ~  first_middle_last_location, data=input_R_v2, family = "binomial")
summary(m_locationfirst_middle_last)#AIC:4082.6 
m_location10_80_10 <- glm(sent_type ~ `10_80_10_percent_location` , data=input_R_v2, family = "binomial")
summary(m_location10_80_10)#AIC:4082.8 

m_location30_40_30_surp <- glm(sent_type ~ `30_40_30_percent_location` + surprisal_first_cs_word_trans, data=input_R_v2, family = "binomial")
summary(m_location30_40_30_surp)#AIC:4040.8
m_location25_50_25_surp <- glm(sent_type ~ `25_50_25_percent_location` + surprisal_first_cs_word_trans, data=input_R_v2, family = "binomial")
summary(m_location25_50_25_surp)#AIC:4040.8
m_locationfirst_middle_last_surp <- glm(sent_type ~ surprisal_first_cs_word_trans + first_middle_last_location, data=input_R_v2, family = "binomial")
summary(m_locationfirst_middle_last_surp)#AIC:4039.3
m_location10_80_10_surp<- glm(sent_type ~ surprisal_first_cs_word_trans + `10_80_10_percent_location` , data=input_R_v2, family = "binomial")
summary(m_location10_80_10_surp)#AIC:4036.5 -->WE USE THIS ONE

ggplot(input_R_v2, aes(first_middle_last_location, ..count..)) + geom_bar(aes(fill = sent_type), position = "dodge")
ggplot(input_R_v2, aes(`10_80_10_percent_location`, ..count..)) + geom_bar(aes(fill = sent_type), position = "dodge")

input_R_v2$location_30_40_30_percent<-input_R_v2$`30_40_30_percent_location`
input_R_v2$location_25_50_25_percent<-input_R_v2$`25_50_25_percent_location`
input_R_v2$location_10_80_10_percent<-input_R_v2$`10_80_10_percent_location`

#######################################################
##### CONTROL MODEL SELECTION - GENETIC ALGORITHM #####
#######################################################

#glmulti(y, xr, data, exclude = c(), name = "glmulti.analysis", intercept = TRUE, marginality = FALSE, bunch=30, chunk = 1, chunks = 1,
#        level = 2, minsize = 0, maxsize = -1, minK = 0, maxK = -1, method = "h", crit = "aic", confsetsize = 100, popsize = 100,
#        mutrate = 10^-3, sexrate = 0.1, imm = 0.3, plotty = TRUE, report = TRUE, deltaM = 0.05, deltaB = 0.05, conseq = 5, fitfunction = "glm", resumefile = "id", includeobjects=TRUE, ...)

#To confirm our selection of the location variable, we use the genetic algorithm with the different encodings of location
location_variables<- c("location_10_80_10_percent","location_25_50_25_percent","location_30_40_30_percent","first_middle_last_location")
result_location <- glmulti(y="sent_type",xr=location_variables,data=input_R_v2,level=1, crit="aic", family="binomial",method="g")
#Best model: sent_type~1+location_10_80_10_percent+first_middle_last_location --->We select 10_80_10
summary(result_location)



variables<- c("location_10_80_10_percent","postag_converted","deprel_first_cs_word_trans","dependency_distance","dependency_distance_left","length_first_cs_word_trans","translation_sentence_length",
              "frequency_negative_ln_first_cs_word_trans")

#INTEREST VARS:  "surprisal_first_cs_word_trans","entropy_at_cs_point"
#NOT USED: "if_previous_word_is_punctuation" (this info is mostly embedded in location),"if_it_is_root" (already contained in deprel_first_cs_word_trans)
#MAYBE??   "bilingual_corpus_frequency_negative_log_first_cs_word_trans" (the size of the corpus is not large enough to give accurate estimates, it gives a very large difference between CS and non-CS, but not sure if reliable)

##################
##### A I C ######
##################

#ONLY MAIN EFFECTS AND NO INTERACTIONS
result_aic_1level <- glmulti(y="sent_type",xr=variables,data=input_R_v2,level=1, crit="aic", family="binomial",method="g")
summary(result_aic_1level)
#Same results in 5 runs:
#Best model: sent_type~1+location_10_80_10_percent+postag_converted+length_first_cs_word_trans+translation_sentence_length+frequency_negative_ln_first_cs_word_trans
#Crit= 3965.48222487052
#No dependency variables selected

#WITH 2-WAY INTERACTIONS
result_aic_2level <- glmulti(y="sent_type",xr=variables,data=input_R_v2,level=2, crit="aic", family="binomial",method="g")
summary(result_aic_2level)
#Out of 7 runs, the algorithm converged 5 runs to: (model a)
#Best model: sent_type~1+postag_converted+length_first_cs_word_trans+translation_sentence_length+frequency_negative_ln_first_cs_word_trans+length_first_cs_word_trans:dependency_distance_left+location_10_80_10_percent:translation_sentence_length+postag_converted:length_first_cs_word_trans+postag_converted:frequency_negative_ln_first_cs_word_trans+deprel_first_cs_word_trans:frequency_negative_ln_first_cs_word_trans
#Crit= 3938.98517300213
#1 run: (model b and c)
#Best model: sent_type~1+postag_converted+length_first_cs_word_trans+translation_sentence_length+frequency_negative_ln_first_cs_word_trans+length_first_cs_word_trans:dependency_distance+length_first_cs_word_trans:dependency_distance_left+frequency_negative_ln_first_cs_word_trans:dependency_distance+frequency_negative_ln_first_cs_word_trans:dependency_distance_left+location_10_80_10_percent:translation_sentence_length+postag_converted:length_first_cs_word_trans+postag_converted:frequency_negative_ln_first_cs_word_trans+deprel_first_cs_word_trans:frequency_negative_ln_first_cs_word_trans
#Crit= 3937.58972419473
#Best model: sent_type~1+location_10_80_10_percent+postag_converted+length_first_cs_word_trans+translation_sentence_length+frequency_negative_ln_first_cs_word_trans+length_first_cs_word_trans:dependency_distance+length_first_cs_word_trans:dependency_distance_left+frequency_negative_ln_first_cs_word_trans:dependency_distance+frequency_negative_ln_first_cs_word_trans:dependency_distance_left+postag_converted:length_first_cs_word_trans+postag_converted:frequency_negative_ln_first_cs_word_trans+deprel_first_cs_word_trans:frequency_negative_ln_first_cs_word_trans
#Crit= 3939.59010916505

m_control_aic2a <-glm(sent_type ~ postag_converted+length_first_cs_word_trans+translation_sentence_length+frequency_negative_ln_first_cs_word_trans+length_first_cs_word_trans:dependency_distance_left+location_10_80_10_percent:translation_sentence_length+postag_converted:length_first_cs_word_trans+postag_converted:frequency_negative_ln_first_cs_word_trans+deprel_first_cs_word_trans:frequency_negative_ln_first_cs_word_trans,
                      data=input_R_v2, family = "binomial")
summary(m_control_aic2a)#AIC 3939
bic(m_control_aic2a)#4052.8
#length_first_cs_word_trans:dependency_distance_left  non significant 
#translation_sentence_length:location_10_80_10_percent2 non significant
#postag_convertedOther:frequency_negative_ln_first_cs_word_trans non significant

m_control_aic2b <-glm(sent_type ~ postag_converted+length_first_cs_word_trans+translation_sentence_length+frequency_negative_ln_first_cs_word_trans+length_first_cs_word_trans:dependency_distance+length_first_cs_word_trans:dependency_distance_left+frequency_negative_ln_first_cs_word_trans:dependency_distance+frequency_negative_ln_first_cs_word_trans:dependency_distance_left+location_10_80_10_percent:translation_sentence_length+postag_converted:length_first_cs_word_trans+postag_converted:frequency_negative_ln_first_cs_word_trans+deprel_first_cs_word_trans:frequency_negative_ln_first_cs_word_trans,
                     data=input_R_v2, family = "binomial")
summary(m_control_aic2b)#AIC 3937.3
bic(m_control_aic2b)#BIC 4069.375
#non significant:
#translation_sentence_length:location_10_80_10_percent2                    -0.52023    0.29408  -1.769 0.076894 .
#postag_convertedVerb:length_first_cs_word_trans                           -0.48478    0.25555  -1.897 0.057828 .  
#postag_convertedOther:length_first_cs_word_trans                          -0.51607    0.26652  -1.936 0.052830 .
#postag_convertedOther:frequency_negative_ln_first_cs_word_trans           -0.04219    0.33144  -0.127 0.898703  

#postag_converted:length_first_cs_word_trans  longer translation words are more likely to be CS if they are nouns
#postag_converted:frequency_negative_ln_first_cs_word_tran  low frequency verbs are less likely to be CS
#deprel_first_cs_word_trans:frequency_negative_ln_first_cs_word_trans compared to baseline (compund:nn) other deprels are less likely to be CS if they are less frequent

##################
##### B I C ######
##################

#ONLY MAIN EFFECTS AND NO INTERACTIONS
result_bic_1level <- glmulti(y="sent_type",xr=variables,data=input_R_v2,level=1, crit="bic", family="binomial",method="g")
summary(result_bic_1level)

#In 10 runs, it always converged to:
#Best model: sent_type~1+postag_converted+length_first_cs_word_trans+translation_sentence_length+frequency_negative_ln_first_cs_word_trans
#Crit= 4003.21226804612

m_control_bic1<-glm(sent_type~1+postag_converted+length_first_cs_word_trans+translation_sentence_length+frequency_negative_ln_first_cs_word_trans , data=input_R_v2, family = "binomial")
summary(m_control_bic1)
#AIC: 3967.3 BIC: 4003.212
bic(m_control_bic1)

#WITH 2-WAY INTERACTIONS
result_bic_2level <- glmulti(y="sent_type",xr=variables,data=input_R_v2,level=2, crit="bic", family="binomial",method="g")
summary(result_bic_2level)
#Out of 10 runs, 5 converged to:
#Best model: sent_type~1+length_first_cs_word_trans+translation_sentence_length+frequency_negative_ln_first_cs_word_trans+postag_converted:length_first_cs_word_trans
#Crit= 4004.70049958192
#5 converged to:
#Best model: sent_type~1+postag_converted+length_first_cs_word_trans+translation_sentence_length+frequency_negative_ln_first_cs_word_trans
#Crit= 4003.21226804612

m_control_bic2_a <-glm(sent_type~1+length_first_cs_word_trans+translation_sentence_length+frequency_negative_ln_first_cs_word_trans+postag_converted:length_first_cs_word_trans, data=input_R_v2, family = "binomial")
summary(m_control_bic2_a)
bic(m_control_bic2_a)
#non significant:
#length_first_cs_word_trans:postag_convertedOther -0.35614    0.22128  -1.609    0.108 
#AIC:3968.8, BIC:4004.7

m_control_bic2_b <-glm(sent_type ~ postag_converted+length_first_cs_word_trans+translation_sentence_length+frequency_negative_ln_first_cs_word_trans, data=input_R_v2, family = "binomial")
summary(m_control_bic2_b)
bic(m_control_bic2_b)
#IDentical to level 1 BIC-> AIC:3967.3 BIC:4003.212 ..EVERYTHING SIGNIFICANT <--- WE SELECT THIS ONE AS CONTROL MODEL

#addding surprisal to the genetic algorithm, to see whether both frequency and surprisal survive
variables_plus_surprisal<- c("location_10_80_10_percent","postag_converted","deprel_first_cs_word_trans","dependency_distance","dependency_distance_left","length_first_cs_word_trans","translation_sentence_length",
              "frequency_negative_ln_first_cs_word_trans","surprisal_first_cs_word_trans")
result_bic2_surprisal <- glmulti(y="sent_type",xr=variables_plus_surprisal,data=input_R_v2,level=2, crit="bic", family="binomial",method="g")
m_control_bic2_surprisal_x <-glm(sent_type~1+postag_converted+length_first_cs_word_trans+translation_sentence_length+surprisal_first_cs_word_trans+surprisal_first_cs_word_trans:length_first_cs_word_trans+surprisal_first_cs_word_trans:frequency_negative_ln_first_cs_word_trans, data=input_R_v2, family = "binomial")
summary(m_control_bic2_surprisal_x)
bic(m_control_bic2_surprisal_x)
#frequency as main effect does not survive, but there is an interaction surprisal:frequency, and surprisal:wordlength

#removing sentence length from the genetic algorithm, in order to see if dependency distance survives (a reviewer suggested dep distance and sentence length are related and therefore dep distance should survive if sentlen is not in the pool)
variables_minus_sentlen<- c("location_10_80_10_percent","postag_converted","deprel_first_cs_word_trans","dependency_distance","dependency_distance_left","length_first_cs_word_trans",
                            "frequency_negative_ln_first_cs_word_trans")
result_bic2_minus_sentlen <- glmulti(y="sent_type",xr=variables_minus_sentlen,data=input_R_v2,level=2, crit="bic", family="binomial",method="g")
#DEP_DISTANCE DOES NOT SURVIVE


##### CONTROL MODEL SELECTED#####
#The new models selected by BIC and AIC differ only in that AIC selects some extra interactions. So we use the BIC model as control model because AIC tends to overfit when the number of 
#datapoints is high.
#sent_type ~ postag_converted+length_first_cs_word_trans+translation_sentence_length+frequency_negative_ln_first_cs_word_trans
#AIC:3967.3 BIC:4003.212
#########################

########## ADDING INTEREST VARIABLES

########SURPRISAL
m_complete_surprisal<-glm(sent_type ~ surprisal_first_cs_word_trans+ postag_converted+length_first_cs_word_trans+translation_sentence_length+frequency_negative_ln_first_cs_word_trans, data=input_R_v2, family = "binomial")
summary(m_complete_surprisal)#AIC 3954.5
bic(m_complete_surprisal) #3996.392
#The model improves adding surprisal, and everything remains significant except frequency
#Coefficients:
#  Estimate Std. Error z value Pr(>|z|)    
#(Intercept)                               -0.13794    0.05001  -2.758 0.005816 ** 
#  surprisal_first_cs_word_trans              0.36936    0.09680   3.816 0.000136 ***
#  postag_convertedVerb                       0.46081    0.10418   4.423 9.73e-06 ***
#  postag_convertedOther                      0.23052    0.11104   2.076 0.037904 *  
#  length_first_cs_word_trans                 0.47227    0.09852   4.794 1.64e-06 ***
#  translation_sentence_length                0.57690    0.07879   7.322 2.44e-13 ***
#  frequency_negative_ln_first_cs_word_trans  0.20735    0.10842   1.912 0.055820 . 


m_complete_devmeansurp<-glm(sent_type ~ deviation_from_mean_surprisal+ postag_converted+length_first_cs_word_trans+translation_sentence_length+frequency_negative_ln_first_cs_word_trans, data=input_R_v2, family = "binomial")
summary(m_complete_devmeansurp) #AIC:3969.2 BIC:4011.166  ####devmean NOT SIGNIFICANT
bic(m_complete_devmeansurp)
#The model gets worse adding devmeansurp... plus, it is not a significant predictor

m_complete_all<-glm(sent_type ~ surprisal_first_cs_word_trans+ deviation_from_mean_surprisal+ postag_converted+length_first_cs_word_trans+translation_sentence_length+frequency_negative_ln_first_cs_word_trans, data=input_R_v2, family = "binomial")
summary(m_complete_all)#AIC:3950.5 BIC:3998.454
bic(m_complete_all)
#The model improves from the control, and everything is significant except postagOther
#However, the model is worse than the model that uses only surprisal

#adding interaction surprisal:devmeansurprisal
m_complete_all_interact<-glm(sent_type ~ surprisal_first_cs_word_trans+ deviation_from_mean_surprisal+ surprisal_first_cs_word_trans*deviation_from_mean_surprisal+ postag_converted+length_first_cs_word_trans+translation_sentence_length+frequency_negative_ln_first_cs_word_trans, data=input_R_v2, family = "binomial")
summary(m_complete_all_interact)#AIC:3945.3 BIC:3999.216
bic(m_complete_all_interact)
#AIC is better than the one with surprisal, but BIC is worse
#devmean is still not significant, does not improve the model


######ADDING ENTROPY
m_complete_entropy<-glm(sent_type ~ entropy_at_cs_point+ postag_converted+length_first_cs_word_trans+translation_sentence_length+frequency_negative_ln_first_cs_word_trans, data=input_R_v2, family = "binomial")
summary(m_complete_entropy)#AIC 3967.9 BIC 4009.829 entropy NOT SIGNIFICANT 
bic(m_complete_entropy)
#AIC AND BIC get worse wrt the baseline

m_complete_entropy_surprisal<-glm(sent_type ~ surprisal_first_cs_word_trans+ entropy_at_cs_point+ postag_converted+length_first_cs_word_trans+translation_sentence_length+frequency_negative_ln_first_cs_word_trans, data=input_R_v2, family = "binomial")
summary(m_complete_entropy_surprisal)#AIC 3956.3 BIC 4004.2 entropy NOT SIGNIFICANT 
bic(m_complete_entropy_surprisal)
#AIC and BIC is worst than using only surprisal
####ENTROPY DOES NOT IMPROVE THE MODEL

m_complete_entropyP1<-glm(sent_type ~ entropy_one_word_after_cs_point  + postag_converted+length_first_cs_word_trans+translation_sentence_length+frequency_negative_ln_first_cs_word_trans, data=input_R_v2, family = "binomial")
summary(m_complete_entropyP1)#AIC 3966.1 BIC 4008.07 entropy not SIGNIFICANT 
bic(m_complete_entropyP1)
#AIC is slightly better than baseline, BIC is worse

m_complete_entropyP1_surprisal<-glm(sent_type ~ surprisal_first_cs_word_trans+ entropy_one_word_after_cs_point  + postag_converted+length_first_cs_word_trans+translation_sentence_length+frequency_negative_ln_first_cs_word_trans, data=input_R_v2, family = "binomial")
summary(m_complete_entropyP1_surprisal)#AIC 3955.8 BIC 4003.678 entropyP1 NOT SIGNIFICANT 
bic(m_complete_entropyP1_surprisal)
#AIC and BIC worse than using only surprisal, plus EntropyP1 is not significant
#EntropyP1 DOES NOT IMPROVE THE MODEL

##########ASSESING WHETHER CORRELATIONS MIGHT BE PROBLEMATIC
vif(m_complete_surprisal)
#                                              GVIF Df GVIF^(1/(2*Df))
#surprisal_first_cs_word_trans             1.545025  1        1.242990
#postag_converted                          1.299302  2        1.067647
#length_first_cs_word_trans                1.634750  1        1.278574
#translation_sentence_length               1.019672  1        1.009788
#frequency_negative_ln_first_cs_word_trans 1.988258  1        1.410056
#ALL VALUES ARE LESS THAN 5 (actually less than 2)--- LITTLE OR NO PROBLEM WITH CORRELATIONS


cor(input_R_v2$surprisal_first_cs_word_trans,input_R_v2$frequency_negative_ln_first_cs_word_trans, method = "spearman")
#0.5935658 #spearman:0.623
cor(input_R_v2$frequency_negative_ln_first_cs_word_trans,input_R_v2$length_first_cs_word_trans, method= "spearman")
#0.5759424 #spearman: 0.5767
cor(input_R_v2$length_first_cs_word_trans,input_R_v2$surprisal_first_cs_word_trans, method = "spearman")
#0.3663939 #spearman: 0.387
cor(input_R_v2$surprisal_first_cs_word_trans,input_R_v2$deviation_from_mean_surprisal, method = "spearman")
#0.649 #spearman=0.636


#########LIKELIHOOD RATIO TESTS
lrtest(m_control_bic1,m_complete_surprisal)
#Df  LogLik Df  Chisq Pr(>Chisq)    
#1   6 -1977.6                         
#2   7 -1970.2  1 14.811  0.0001189 ***   SIGNIFICANT 
lrtest(m_control_bic1,m_complete_entropy)
#Df  LogLik Df  Chisq Pr(>Chisq)
#1   6 -1977.6                     
#2   7 -1977.0  1 1.3731     0.2413   NOT SIGNIFICANT -- ENTROPY DOES NOT IMPROVE BASELINE
lrtest(m_control_bic1,m_complete_devmeansurp)
#Df  LogLik Df  Chisq Pr(>Chisq)
#1   6 -1977.6                     
#2   7 -1977.6  1 0.0361     0.8492   NOT SIGNIFICANT -- DEVIATION FROM MEAN SURP DOES NOT IMPROVE BASELINE
lrtest(m_control_bic1,m_complete_entropyP1)
#1   6 -1977.6                       
#2   7 -1976.1  1 3.1323    0.07676 . #VERY SMALL EVIDENCE THAT ENTROPYP1 MIGHT IMPROVE BASELINE
lrtest(m_complete_surprisal,m_complete_entropy_surprisal)
#Df  LogLik Df  Chisq Pr(>Chisq)
#1   7 -1970.2                     
#2   8 -1970.1  1 0.1819     0.6698  #NOT SIGNIFICANT-- ENTROPY DOES NOT IMPROVE A MODEL THAT HAS SURPRISAL ALREADY
lrtest(m_complete_surprisal,m_complete_entropyP1_surprisal)
#Df  LogLik Df  Chisq Pr(>Chisq)
#1   7 -1970.2                     
#2   8 -1969.9  1 0.7039     0.4015  #NOT SIGNIFICANT-- ENTROPYP1 DOES NOT IMPROVE A MODEL THAT HAS SURPRISAL ALREADY

###########PLOTTING PREDICTIONS
predicted.data<-data.frame(probability_of_cs=m_complete_surprisal$fitted.values, cs=input_R_v2$sent_type)
predicted.data<-predicted.data[order(predicted.data$probability_of_cs, decreasing=FALSE),] 
predicted.data$rank <-1:nrow(predicted.data)
ggplot(data=predicted.data, aes(x=rank, y=probability_of_cs))+geom_point(aes(color=cs), alpha=1,shape=3,stroke=1)+ xlab("Index")+ ylab("Predicted probability of CS")
ggsave("code_switching_probs.pdf")

#LIKELIHOOD RATIO TESTS... INDIVIDUAL ABLATIONS
#ALL MINUS POSTAG
m_complete_surprisal_nopostag<-glm(sent_type ~ surprisal_first_cs_word_trans+length_first_cs_word_trans+translation_sentence_length+frequency_negative_ln_first_cs_word_trans, data=input_R_v2, family = "binomial")
summary(m_complete_surprisal_nopostag)#AIC 3971 BIC 4000.937
bic(m_complete_surprisal_nopostag)
lrtest(m_complete_surprisal_nopostag,m_complete_surprisal)
#Df  LogLik Df  Chisq Pr(>Chisq)    
#1   5 -1980.5                         
#2   7 -1970.2  2 20.525  3.492e-05 ***

#ALL MINUS FREQUENCY
m_complete_surprisal_nofreq<-glm(sent_type ~ surprisal_first_cs_word_trans+postag_converted+ length_first_cs_word_trans+translation_sentence_length, data=input_R_v2, family = "binomial")
summary(m_complete_surprisal_nofreq)#AIC 3956.1 BIC 3992.071
bic(m_complete_surprisal_nofreq)
lrtest(m_complete_surprisal_nofreq,m_complete_surprisal)
#Df  LogLik Df Chisq Pr(>Chisq)  
#1   6 -1972.1                      
#2   7 -1970.2  1 3.669    0.05543 .

#ALL MINUS WORD LENGTH
m_complete_surprisal_nowlength<-glm(sent_type ~ surprisal_first_cs_word_trans+postag_converted+ translation_sentence_length + frequency_negative_ln_first_cs_word_trans, data=input_R_v2, family = "binomial")
summary(m_complete_surprisal_nowlength)#AIC 3975.8 BIC 4011.744
bic(m_complete_surprisal_nowlength)
lrtest(m_complete_surprisal_nowlength,m_complete_surprisal)
#Df  LogLik Df  Chisq Pr(>Chisq)    
#1   6 -1981.9                         
#2   7 -1970.2  1 23.342  1.356e-06 ***

#ALL MINUS SENTENCE LENGTH
m_complete_surprisal_noSlength<-glm(sent_type ~ surprisal_first_cs_word_trans+postag_converted+ length_first_cs_word_trans + frequency_negative_ln_first_cs_word_trans, data=input_R_v2, family = "binomial")
summary(m_complete_surprisal_noSlength)#AIC 4008.6 BIC 4044.504
bic(m_complete_surprisal_noSlength)
lrtest(m_complete_surprisal_noSlength,m_complete_surprisal)
#Df  LogLik Df  Chisq Pr(>Chisq)    
#1   6 -1998.3                         
#2   7 -1970.2  1 56.103  6.878e-14 ***

