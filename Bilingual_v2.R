#install.packages("car")
#install.packages("readr")
#install.packages("glmulti")
#install.packages("rJava")
#install.packages("lmtest")
library(ggplot2)
library(dplyr)
library(car)
library(readr)
library(glmulti)
library(lmtest)

input_R_v2 <- read_csv("/Users/jzc1104/Documents/Projects/Le Fang - CodeSwitching/MixedModeling/03082019_location_appended_input_R_v2.csv", 
                       col_types = cols(sent_type = col_factor(levels = c("non-code-switch", "code-switch")),
                                        sent_id = col_integer(), 
                                        university = col_factor(levels = c("CMU","PIT", "PSU")), 
                                        
                                        #Categorical
                                          #location
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
                                        bilingual_corpus_frequency_negative_log_first_cs_word=col_double(),
                                        bilingual_corpus_frequency_negative_log_first_cs_word_trans=col_double(),
                                        
                                        length_first_cs_word_form = col_integer(), 
                                        length_first_cs_word_trans = col_integer(), 
                                        dependency_distance = col_integer(), 
                                        translation_sentence_length = col_integer()
                                        )
                       , trim_ws = FALSE)
#view data
View(input_R_v2)
#view data head
head(input_R_v2)
#view data details
str(input_R_v2)
#count rows of data frame
nrow(input_R_v2)

plot(input_R_v2$sent_type, input_R_v2$length_first_cs_word_trans)
plot(input_R_v2$sent_type,input_R_v2$translation_sentence_length)
plot(input_R_v2$deprel_first_cs_word_trans,input_R_v2$length_first_cs_word_trans)
myvars <- c("sent_type", "first_cs_word_translation", "length_first_cs_word_trans")
new_view<-input_R_v2[myvars]
View(new_view)

###PREPROCESSING OF POSTAGS###
#Get Unique postags
unique(input_R_v2$pos_tag_first_cs_word_trans)
#https://www.sketchengine.eu/chinese-penn-treebank-part-of-speech-tagset/
#convert all postags that are NR (proper noun), NN (common noun) or NT (temporal noun) to "Noun"
input_R_v2$pos_tag_first_cs_word_trans[which(input_R_v2$pos_tag_first_cs_word_trans=='NR')]<-"Noun" 
input_R_v2$pos_tag_first_cs_word_trans[which(input_R_v2$pos_tag_first_cs_word_trans=='NN')]<-"Noun" 
input_R_v2$pos_tag_first_cs_word_trans[which(input_R_v2$pos_tag_first_cs_word_trans=='NT')]<-"Noun" 

#convert all postags that are VE (you:be,have) or VV (other verb) to "Verb"
input_R_v2$pos_tag_first_cs_word_trans[which(input_R_v2$pos_tag_first_cs_word_trans=='VE')]<-"Verb" 
input_R_v2$pos_tag_first_cs_word_trans[which(input_R_v2$pos_tag_first_cs_word_trans=='VV')]<-"Verb" 

#convert other postags as "Other"
input_R_v2$pos_tag_first_cs_word_trans[which(input_R_v2$pos_tag_first_cs_word_trans!='Verb' & input_R_v2$pos_tag_first_cs_word_trans!='Noun' )] <- "Other" 

input_R_v2$pos_tag_first_cs_word_trans<- as.factor(input_R_v2$pos_tag_first_cs_word_trans)
#review levels
levels(input_R_v2$pos_tag_first_cs_word_trans)
levels(input_R_v2$pos_tag_first_cs_word_trans) <- c("Noun","Verb", "Other") #Not sure if order matters

#Plot the distribution of postags
ggplot(input_R_v2, aes(pos_tag_first_cs_word_trans, ..count..)) + geom_bar(aes(fill = sent_type), position = "dodge")

###DEPENDENCY RELATIONS###
#Get unique dependencies
unique(input_R_v2$deprel_first_cs_word_trans)
ggplot(input_R_v2, aes(deprel_first_cs_word_trans, ..count..)) + geom_bar(aes(fill = sent_type), position = "dodge")
summary(input_R_v2$deprel_first_cs_word_trans)

#Remove the dependencies with less than 100 atestations of CS or non-CS
input_R_v2$deprel_first_cs_word_trans[which(input_R_v2$deprel_first_cs_word_trans!='compound:nn' & input_R_v2$deprel_first_cs_word_trans!='nsubj' & input_R_v2$deprel_first_cs_word_trans!='dobj'  & input_R_v2$deprel_first_cs_word_trans!='root' &  input_R_v2$deprel_first_cs_word_trans!='dep' & input_R_v2$deprel_first_cs_word_trans!='amod')] <- "Other" 
input_R_v2$deprel_first_cs_word_trans<- as.factor(input_R_v2$deprel_first_cs_word_trans)
levels(input_R_v2$deprel_first_cs_word_trans)<-c("compound:nn","nsubj","dobj","root","dep","amod","Other")


###IF WORD IS AT THE BEGINNING, PREVIOUS WORD IS PUNCTUATION IS CONSIDERED TRUE
input_R_v2$if_previous_word_is_punctuation [is.na(input_R_v2$if_previous_word_is_punctuation)] <- TRUE

###AVOID INFINITE SURPRISALS####
#count rows of frequency_negative_ln_first_cs_word_trans, where it is NA
nrow(input_R_v2[is.na(input_R_v2$frequency_negative_ln_first_cs_word_trans),])
View(input_R_v2[is.na(input_R_v2$frequency_negative_ln_first_cs_word_trans),])
#By looking at the sentences, those rows seem to be very bad, so we remove them
input_R_v2<-input_R_v2[!is.na(input_R_v2$frequency_negative_ln_first_cs_word_trans),]

#get a colnum with only non-inf value
surp_colnum <- input_R_v2$surprisal_first_cs_word_trans[which(input_R_v2$surprisal_first_cs_word_trans < Inf)]
#get the max surprisal
max(surp_colnum)
#replace inf with max surprisal
input_R_v2$surprisal_first_cs_word_trans[is.infinite(input_R_v2$surprisal_first_cs_word_trans)] <- 10.16642
plot(input_R_v2$sent_type,input_R_v2$surprisal_first_cs_word_trans)

#DEVIATION FROM MEAN SURPRISAL
input_R_v2$deviation_from_mean_surprisal <-abs(input_R_v2$average_surprisal-input_R_v2$surprisal_first_cs_word_trans)
myvars <- c("surprisal_first_cs_word_trans", "average_surprisal", "deviation_from_mean_surprisal")
new_view<-input_R_v2[myvars]
View(new_view)

#DEVIATION FROM LAST WORD'S SURPRISAL
input_R_v2$deviation_from_lastword_surprisal <-abs(input_R_v2$surprisal_of_previous_word-input_R_v2$surprisal_first_cs_word_trans)
myvars <- c("surprisal_first_cs_word_trans", "surprisal_of_previous_word", "surprisal_first_cs_word_trans")
new_view<-input_R_v2[myvars]
View(new_view)


nrow(input_R_v2)
#There shouldn't be any more NAs
nrow(input_R_v2[!(is.na(input_R_v2$frequency_negative_ln_first_cs_word_trans)),])
#input_R_v2 <- input_R_v2[!(is.na(input_R_v2$frequency_negative_ln_first_cs_word_trans)),]

###STANDARDIZATION###
#not used:
input_R_v2$average_surprisal<-scale(input_R_v2$average_surprisal,center = TRUE, scale=TRUE) #not significant
input_R_v2$surprisal_of_previous_word<-scale(input_R_v2$surprisal_of_previous_word,center=TRUE, scale=TRUE) #not significant
input_R_v2$deviation_from_lastword_surprisal<-scale(input_R_v2$deviation_from_lastword_surprisal,center = TRUE, scale = TRUE) #not significant... plus the notion of surprisal is related to the last word; and also this is not defined for initial words

input_R_v2$bilingual_corpus_frequency_negative_log_first_cs_word_trans<-scale(input_R_v2$bilingual_corpus_frequency_negative_log_first_cs_word_trans,center=TRUE,scale=TRUE)
input_R_v2$bilingual_corpus_frequency_negative_log_first_cs_word_trans<-input_R_v2$bilingual_corpus_frequency_negative_log_first_cs_word_trans * 0.5

#used
input_R_v2$frequency_negative_ln_first_cs_word_trans <- scale(input_R_v2$frequency_negative_ln_first_cs_word_trans, center=TRUE, scale=TRUE)
input_R_v2$frequency_negative_ln_first_cs_word_trans <-input_R_v2$frequency_negative_ln_first_cs_word_trans *0.5
input_R_v2$surprisal_first_cs_word_trans <- scale(input_R_v2$surprisal_first_cs_word_trans, center=TRUE, scale=TRUE)
input_R_v2$surprisal_first_cs_word_trans <-input_R_v2$surprisal_first_cs_word_trans *0.5
input_R_v2$deviation_from_mean_surprisal<-scale(input_R_v2$deviation_from_mean_surprisal,center = TRUE, scale = TRUE)
input_R_v2$deviation_from_mean_surprisal<-input_R_v2$deviation_from_mean_surprisal *0.5


input_R_v2$length_first_cs_word_trans <-scale(input_R_v2$length_first_cs_word_trans, center = TRUE, scale = TRUE)
input_R_v2$length_first_cs_word_trans <-input_R_v2$length_first_cs_word_trans *0.5

input_R_v2$dependency_distance <-scale(input_R_v2$dependency_distance, center = TRUE, scale = TRUE)
input_R_v2$dependency_distance <-input_R_v2$dependency_distance *0.5
input_R_v2$translation_sentence_length <- scale(input_R_v2$translation_sentence_length, center = TRUE, scale=TRUE)
input_R_v2$translation_sentence_length <-input_R_v2$translation_sentence_length *0.5


###SOME MODELLING TO SELECT PREDICTORS###

#Only surprisal -->significant
m_surp <- glm(sent_type ~ surprisal_first_cs_word_trans, data=input_R_v2, family="binomial")
summary(m_surp)

#Surprisal and Average Surprisal--> putting surprisal Average surprisal is not significant --->>we drop average surprisal
m_aversurp_surp <- glm(sent_type ~ surprisal_first_cs_word_trans + average_surprisal, data=input_R_v2, family="binomial")
summary(m_aversurp_surp)

#Surprisal and Deviation from Mean surprisal -->both significant
m_differ_aversurp <- glm(sent_type ~ surprisal_first_cs_word_trans + deviation_from_mean_surprisal, data=input_R_v2, family="binomial")
summary(m_differ_aversurp)

#Surprisal and Last word's surprisal and las word surprisal deviation -->both not significant
m_las_word <- glm(sent_type ~ surprisal_first_cs_word_trans + surprisal_of_previous_word, data=input_R_v2, family="binomial")
summary(m_las_word)
m_lastword_deviation<- glm(sent_type ~ surprisal_first_cs_word_trans + deviation_from_lastword_surprisal, data=input_R_v2, family="binomial")
summary(m_lastword_deviation)

#Only frequency -->significant
m_freq <- glm(sent_type ~ frequency_negative_ln_first_cs_word_trans, data=input_R_v2, family="binomial")
summary(m_freq)

#Surprisal and frequency -->both significant
m_surp_freq <- glm(sent_type ~ surprisal_first_cs_word_trans + frequency_negative_ln_first_cs_word_trans, data=input_R_v2, family="binomial")
summary(m_surp_freq)

#Having the local surprisal seems to improve things rather than the wikipedia surprisal
m_local_freq<- glm(sent_type ~ surprisal_first_cs_word_trans + frequency_negative_ln_first_cs_word_trans + bilingual_corpus_frequency_negative_log_first_cs_word_trans, data=input_R_v2, family="binomial")
summary(m_local_freq)


#Postag -->not significant
m_pos <- glm(sent_type ~ pos_tag_first_cs_word_trans, data=input_R_v2, family = "binomial")
summary(m_pos)
plot(input_R_v2$pos_tag_first_cs_word_trans,input_R_v2$surprisal_first_cs_word_trans )

Anova(m_pos)

#Surprisal and POS --> pos not significant
m_surp_pos <- glm(sent_type ~ surprisal_first_cs_word_trans + pos_tag_first_cs_word_trans, data=input_R_v2, family = "binomial")
model.matrix(input_R_v2$sent_type ~ input_R_v2$surprisal_first_cs_word_trans + input_R_v2$pos_tag_first_cs_word_trans)
summary(m_surp_pos)

#Surprisal, Frequency and POS with interaction --> some significance of surprisal:other
m_surp_x_pos <- glm(sent_type ~ surprisal_first_cs_word_trans * pos_tag_first_cs_word_trans + frequency_negative_ln_first_cs_word_trans*pos_tag_first_cs_word_trans, data=input_R_v2, family = "binomial")
summary(m_surp_x_pos)

#Word Length of the translation-->significant
m_wordlength <- glm(sent_type ~ length_first_cs_word_trans, data=input_R_v2, family = "binomial")
summary(m_wordlength)
plot(input_R_v2$sent_type,input_R_v2$length_first_cs_word_trans)

#Whether the CS_word is the root of the sentence -->not significant
m_root <- glm(sent_type ~ if_it_is_root, data=input_R_v2, family = "binomial")
summary(m_root)
View(input_R_v2$if_it_is_root)
View(input_R_v2$deprel_first_cs_word_trans)

#Dependency distance -->not significant
plot(input_R_v2$sent_type,input_R_v2$dependency_distance)
m_depdistance <- glm(sent_type ~ dependency_distance, data=input_R_v2, family = "binomial")
summary(m_depdistance)

plot(input_R_v2$sent_type,input_R_v2$translation_sentence_length)
m_sentlen <-glm(sent_type~ surprisal_first_cs_word_trans + translation_sentence_length, data=input_R_v2, family = "binomial")
summary(m_sentlen)

m_surp_posxlen <- glm(sent_type ~ surprisal_first_cs_word_trans + pos_tag_first_cs_word_trans*length_first_cs_word_trans, data=input_R_v2, family = "binomial")
summary(m_surp_posxlen)

######LOCATION 
m_location30_40_30 <- glm(sent_type ~ `30_40_30_percent_location` , data=input_R_v2, family = "binomial")
summary(m_location30_40_30)#AIC:4655.3
m_location25_50_25 <- glm(sent_type ~ `25_50_25_percent_location` , data=input_R_v2, family = "binomial")
summary(m_location25_50_25)#AIC:4652.4

m_locationfirst_middle_last <- glm(sent_type ~ surprisal_first_cs_word_trans + first_middle_last_location, data=input_R_v2, family = "binomial")
summary(m_locationfirst_middle_last)#AIC:4640.7 (without surp) 4562.1 (with surprisal)

m_location10_80_10 <- glm(sent_type ~ surprisal_first_cs_word_trans + `10_80_10_percent_location` , data=input_R_v2, family = "binomial")
summary(m_location10_80_10)#AIC:4634.8(without surp) 4552 (with surprisal)  -->>>>WINNER!

ggplot(input_R_v2, aes(`10_80_10_percent_location`, ..count..)) + geom_bar(aes(fill = sent_type), position = "dodge")
ggplot(input_R_v2, aes(first_middle_last_location, ..count..)) + geom_bar(aes(fill = sent_type), position = "dodge")


input_R_v2$location_10_80_10_percent<-input_R_v2$`10_80_10_percent_location`





##############################################
#####MODEL SELECTION - GENETIC ALGORITHM #####
##############################################

#glmulti(y, xr, data, exclude = c(), name = "glmulti.analysis", intercept = TRUE, marginality = FALSE, bunch=30, chunk = 1, chunks = 1,
#        level = 2, minsize = 0, maxsize = -1, minK = 0, maxK = -1, method = "h", crit = "aic", confsetsize = 100, popsize = 100,
#        mutrate = 10^-3, sexrate = 0.1, imm = 0.3, plotty = TRUE, report = TRUE, deltaM = 0.05, deltaB = 0.05, conseq = 5, fitfunction = "glm", resumefile = "id", includeobjects=TRUE, ...)

variables<- c("location_10_80_10_percent","pos_tag_first_cs_word_trans","deprel_first_cs_word_trans","dependency_distance","length_first_cs_word_trans",   
              "frequency_negative_ln_first_cs_word_trans","translation_sentence_length")

#INTEREST VARS:  "surprisal_first_cs_word_trans","deviation_from_mean_surprisal"
#NOT USED: "if_previous_word_is_punctuation" (this info is mostly embedded in location),"if_it_is_root" (already contained in deprel_first_cs_word_trans), 
#MAYBE??   "bilingual_corpus_frequency_negative_log_first_cs_word_trans" (the size of the corpus is not large enough to give accurate estimates, it gives a very large difference between CS and non-CS, but not sure if reliable)

result_aic_1level <- glmulti(y="sent_type",xr=variables,data=input_R_v2,level=1, crit="aic", family="binomial",method="g")
summary(result_aic_1level)
#Best model: sent_type~1+location_10_80_10_percent+pos_tag_first_cs_word_trans+deprel_first_cs_word_trans+length_first_cs_word_trans+frequency_negative_ln_first_cs_word_trans+translation_sentence_length
#Best model: sent_type~1+location_10_80_10_percent+pos_tag_first_cs_word_trans+deprel_first_cs_word_trans+length_first_cs_word_trans+frequency_negative_ln_first_cs_word_trans+translation_sentence_length
#Best model: sent_type~1+location_10_80_10_percent+pos_tag_first_cs_word_trans+deprel_first_cs_word_trans+length_first_cs_word_trans+frequency_negative_ln_first_cs_word_trans+translation_sentence_length
#Best model: sent_type~1+location_10_80_10_percent+pos_tag_first_cs_word_trans+deprel_first_cs_word_trans+length_first_cs_word_trans+frequency_negative_ln_first_cs_word_trans+translation_sentence_length
#Best model: sent_type~1+location_10_80_10_percent+pos_tag_first_cs_word_trans+deprel_first_cs_word_trans+length_first_cs_word_trans+frequency_negative_ln_first_cs_word_trans+translation_sentence_length
#Best model: sent_type~1+location_10_80_10_percent+pos_tag_first_cs_word_trans+deprel_first_cs_word_trans+length_first_cs_word_trans+frequency_negative_ln_first_cs_word_trans+translation_sentence_length
#Crit= 4505.51529252526
#dependency distance not selected

result_aic_2level <- glmulti(y="sent_type",xr=variables,data=input_R_v2,level=2, crit="aic", family="binomial",method="g")
summary(result_aic_2level)
  #without word_length: #location_10_80_10_percent+pos_tag_first_cs_word_trans+deprel_first_cs_word_trans+frequency_negative_ln_first_cs_word_trans+translation_sentence_length+deprel_first_cs_word_trans:pos_tag_first_cs_word_trans+pos_tag_first_cs_word_trans:frequency_negative_ln_first_cs_word_trans+deprel_first_cs_word_trans:frequency_negative_ln_first_cs_word_trans

#Best model: sent_type~1+location_10_80_10_percent+pos_tag_first_cs_word_trans+deprel_first_cs_word_trans+length_first_cs_word_trans+frequency_negative_ln_first_cs_word_trans+translation_sentence_length+deprel_first_cs_word_trans:pos_tag_first_cs_word_trans+pos_tag_first_cs_word_trans:length_first_cs_word_trans+deprel_first_cs_word_trans:length_first_cs_word_trans
                        #location_10_80_10_percent+pos_tag_first_cs_word_trans+deprel_first_cs_word_trans+length_first_cs_word_trans+frequency_negative_ln_first_cs_word_trans+translation_sentence_length+deprel_first_cs_word_trans:pos_tag_first_cs_word_trans+pos_tag_first_cs_word_trans:length_first_cs_word_trans+deprel_first_cs_word_trans:length_first_cs_word_trans
                        #location_10_80_10_percent+pos_tag_first_cs_word_trans+deprel_first_cs_word_trans+length_first_cs_word_trans+frequency_negative_ln_first_cs_word_trans+translation_sentence_length+deprel_first_cs_word_trans:pos_tag_first_cs_word_trans+pos_tag_first_cs_word_trans:length_first_cs_word_trans+deprel_first_cs_word_trans:length_first_cs_word_trans
                        #location_10_80_10_percent+pos_tag_first_cs_word_trans+deprel_first_cs_word_trans+length_first_cs_word_trans+frequency_negative_ln_first_cs_word_trans+translation_sentence_length+deprel_first_cs_word_trans:pos_tag_first_cs_word_trans+pos_tag_first_cs_word_trans:length_first_cs_word_trans+deprel_first_cs_word_trans:length_first_cs_word_trans
                        #location_10_80_10_percent+pos_tag_first_cs_word_trans+deprel_first_cs_word_trans+length_first_cs_word_trans+frequency_negative_ln_first_cs_word_trans+translation_sentence_length+deprel_first_cs_word_trans:pos_tag_first_cs_word_trans+pos_tag_first_cs_word_trans:length_first_cs_word_trans+deprel_first_cs_word_trans:length_first_cs_word_trans
#Crit= 4484.96682299668
#Mean crit= 4499.69638118431
m_control_aic2 <-glm(sent_type ~ location_10_80_10_percent+pos_tag_first_cs_word_trans+deprel_first_cs_word_trans+length_first_cs_word_trans+frequency_negative_ln_first_cs_word_trans+translation_sentence_length+deprel_first_cs_word_trans:pos_tag_first_cs_word_trans+pos_tag_first_cs_word_trans:length_first_cs_word_trans+deprel_first_cs_word_trans:length_first_cs_word_trans,
                      data=input_R_v2, family = "binomial")
#deprel_first_cs_word_trans:pos_tag_first_cs_word_trans avoid this interaction because there are pairs with NA, plust it's not significant
#deprel_first_cs_word_trans:length_first_cs_word_trans--- not sure what it means
#+pos_tag_first_cs_word_trans:length_first_cs_word_trans--- not sure what it means
summary(m_control_aic2)

result_bic_1level <- glmulti(y="sent_type",xr=variables,data=input_R_v2,level=1, crit="bic", family="binomial",method="g")
summary(result_bic_1level)
#Best model: sent_type~1+frequency_negative_ln_first_cs_word_trans+translation_sentence_length #This is always the best model (5 runs)
#Crit= 4564.79458429897
m_control_bic1<-glm(sent_type ~frequency_negative_ln_first_cs_word_trans+translation_sentence_length  , data=input_R_v2, family = "binomial")
summary(m_control_bic1)

result_bic_2level <- glmulti(y="sent_type",xr=variables,data=input_R_v2,level=2, crit="bic", family="binomial",method="g")
summary(result_bic_2level)
#ALWAYS CONVERGES TO THIS (6 ATTEMPTS)
#Best model: sent_type~1+frequency_negative_ln_first_cs_word_trans+translation_sentence_length
#Crit= 4564.79458429897

m_control_bic2 <-glm(sent_type ~ frequency_negative_ln_first_cs_word_trans+translation_sentence_length, data=input_R_v2, family = "binomial")
summary(m_control_bic2)

#SINCE AIC AND BIC DIFFER IN THE SELECTED MODELS, WE USE THE MODEL OBTAINED WITH AIC BUT REMOVING THE INTERACTIONS (which is something in between the models of AIC and BIC), THAT MODEL WILL BE USED AS CONTROL
m_control <-glm(sent_type ~ location_10_80_10_percent+pos_tag_first_cs_word_trans+deprel_first_cs_word_trans+length_first_cs_word_trans+frequency_negative_ln_first_cs_word_trans+translation_sentence_length,
                    data=input_R_v2, family = "binomial")
summary(m_control)#AIC: 4505


m_complete_surprisal<-glm(sent_type ~ surprisal_first_cs_word_trans+ location_10_80_10_percent+pos_tag_first_cs_word_trans+deprel_first_cs_word_trans+length_first_cs_word_trans+frequency_negative_ln_first_cs_word_trans+translation_sentence_length, data=input_R_v2, family = "binomial")
summary(m_complete_surprisal)#AIC_ 4470.8

m_complete_devmeansurp<-glm(sent_type ~ deviation_from_mean_surprisal+ location_10_80_10_percent+pos_tag_first_cs_word_trans+deprel_first_cs_word_trans+length_first_cs_word_trans+frequency_negative_ln_first_cs_word_trans+translation_sentence_length, data=input_R_v2, family = "binomial")
summary(m_complete_devmeansurp) #AIC:4507.4  ####devmean NOT SIGNIFICANT

m_complete_all<-glm(sent_type ~ surprisal_first_cs_word_trans+ deviation_from_mean_surprisal+ location_10_80_10_percent+pos_tag_first_cs_word_trans+deprel_first_cs_word_trans+length_first_cs_word_trans+frequency_negative_ln_first_cs_word_trans+translation_sentence_length, data=input_R_v2, family = "binomial")
summary(m_complete_all)#AIC:4457.6

vif(m_complete_all)

input_R_v2$dummy_sent<-ifelse(test=input_R_v2$sent_type == "code-switch", yes=1, no=0)
m_complete_all_dummy<-lm(dummy_sent~ surprisal_first_cs_word_trans+ deviation_from_mean_surprisal+ location_10_80_10_percent+pos_tag_first_cs_word_trans+deprel_first_cs_word_trans+length_first_cs_word_trans+frequency_negative_ln_first_cs_word_trans+translation_sentence_length, data=input_R_v2)
summary(m_complete_all_dummy)
vif(m_complete_all_dummy)

cor(input_R_v2$surprisal_first_cs_word_trans,input_R_v2$frequency_negative_ln_first_cs_word_trans)
cor(input_R_v2$surprisal_first_cs_word_trans,input_R_v2$deviation_from_mean_surprisal)

lrtest(m_control,m_complete_surprisal)
#Df  LogLik Df  Chisq Pr(>Chisq)    
#1  14 -2238.8                         
#2  15 -2220.4  1 36.695  1.381e-09 ***
lrtest(m_control,m_complete_devmeansurp) #NOT SIGNIFICANT
#Df  LogLik Df  Chisq Pr(>Chisq)    
#1  14 -2238.8                     
#2  15 -2238.7  1 0.0933     0.7601
lrtest(m_control,m_complete_all)  
#Df  LogLik Df  Chisq Pr(>Chisq)    
#1  14 -2238.8                         
#2  16 -2212.8  2 51.927  5.299e-12 ***
lrtest(m_complete_surprisal,m_complete_all)
#Df  LogLik Df  Chisq Pr(>Chisq)    
#1  15 -2220.4                         
#2  16 -2212.8  1 15.232  9.508e-05 ***
nrow(input_R_v2)

