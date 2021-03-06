# Surprisal Predicts Code-Switching in Chinese-English Bilingual Text

 This project contains the code to obtain and analyze the data used in the paper: [Surprisal Predicts Code-Switching in Chinese-English Bilingual Text]( https://www.aclweb.org/anthology/2020.emnlp-main.330.pdf
), presented at the 2020 Conference on Empirical Methods in Natural Language Processing. Here we also make available the resulting Chinese-English Code-Switching corpus.
 
 
 >ABSTRACT:
 Why do bilinguals switch languages within
a sentence? The present observational study
asks whether word surprisal and word entropy predict code-switching in bilingual written conversation. We describe and model a
new dataset of Chinese-English text with 1476
clean code-switched sentences, translated back
into Chinese. The model includes known
control variables together with word surprisal
and word entropy. We found that word surprisal, but not entropy, is a significant predictor that explains code-switching above and beyond other well-known predictors. We also
found sentence length to be a significant predictor, which has been related to sentence complexity. We propose high cognitive effort as a
reason for code-switching, as it leaves fewer
resources for inhibition of the alternative language. We also corroborate previous findings,
but this time using a computational model of
surprisal, a new language pair, and doing so
for written language.
 
 If you use the code or data, please include a citation to the paper:
 ```
@inproceedings{codeswitching2020surprisal,
  title={Surprisal Predicts Code-Switching in Chinese-English Bilingual Text},
  author={Calvillo, Jes{\'u}s and Fang, Le and Cole, Jeremy and Reitter, David},
  booktitle={Proceedings of the 2020 Conference on Empirical Methods in Natural Language Processing (EMNLP)},  
  pages={4029--4039}, 
  year={2020}
}
```
If you would like to reproduce the statistical analysis, the file stat_analysis.R contains the R code that we used. The data used in the analysis is contained in the file [11152019_two_entropies_appended_input_R_v4.csv](https://github.com/lfang1/CodeSwitchingResearch/blob/master/11152019_two_entropies_appended_input_R_v4.csv).

## Corpus of Chinese-English Code-switched Sentences

The corpus collected for the analysis is contained in the file [11152019_two_entropies_appended_input_R_v4.csv](https://github.com/lfang1/CodeSwitchingResearch/blob/master/11152019_two_entropies_appended_input_R_v4.csv). This file contains 1476 Chinese-English code-switched sentences that were manually cleaned and translated into Chinese. Each sentence is also coupled with a Chinese sentence that was never code-switched and that has a similar syntactic structure to the code-switched one. 

Each sentence is annotated with the following variables (those with cs-point in parentheses are related to only the first word that is code-switched):

* translation into Chinese
* first code-switched word and its translation
* negative log of frequency (cs-point)
* word surprisal (cs-point)
* POS-tag (cs-point)
* index of dependency governor (cs-point)
* dependency relation to governor (cs-point)
* surprisal values
* average_surprisal
* negative log of frequency using only the corpus (bilingual_corpus_frequency_negative_log_first_cs_word)
* negative log of frequency using only the corpus of the translation (bilingual_corpus_frequency_negative_log_first_cs_word_trans)
* original word length (cs-point)
* translation word length (cs-point)
* dependency distance to governor (cs-point)
* if it is root (binary, cs-point)
* if previous word is punctuation (binary, cs-point)
* surprisal of previous word (cs-point)
* various encodings of location (cs-point)
* sentence length
* word entropy (cs-point)
* word entropy after cs-point

Word surprisal and entropy values were obtained from a 5-gram language model trained on the Chinese Wikipedia and with the SRILM framework.
POS-tags and dependeny relations were obtained with the Stanford Parser.

## Data Collection Process

This document describes the procedure that we followed to collect the corpus and obtain the measurements related to the control factors for our code-switching research. The following is a step-by-step instruction.

Step 1: 
Run the following three python programs were written to scrap text from online forum.
webscrap-cmucssa-v1.py; webscrap-pittcssa-v1.py; webscrap-psucssa-v1.py

A raw Chinese-English corpus was collected by scraping text from Chinese Students and Scholars Association Bulletin Board System (CSSA BBS) of three universities, which are Pennsylvanina State University, Carnegie Mellon University, and University of Pittsburgh.

Step 2: 
After the raw corpus was collected, Stanford Segementer was used to tokenize the words in the sentences. In order to identify code-switching in the corpus, a Chinese Dictionary and an English Dictionary were built based on Google 1-gram corpus. 

Step 3:
By referring to the two dictionaries (in the folder data/independent.txt), the following program distinguishes if a word is English or Chinese and get its relative frequency and then save code-switched sentence with details line-by-line as CSV file.
CodeSwitchedSentenceFinder.java

Step 4:
We combined all the sentences that contain code-switching into a code-switched corpus. Then I hired four international Chinese undergraduate students to translated the code-switched corpus into Chinese. The translation contains both word-by-word translation and the whole-sentence translation. The quality of the translation was verified by hiring qualified Chinese-English bilingual workers from Amazon Mechanical Turk.

Step 5:
The following codes were written to get a refined corpus by cleaning and merging the translated sentences with the original sentences, and getting location indices of each code-switched word and punctuation in the translated sentences.
AddNewVariables.java; AddMissingPeriod.java; AddIdLine.java; AddCSTypeAndSentenceType.java;
RefineCorpus.java; Remove Duplication.java; CleanNonCSSentence.java; FixErrorInCorpus.java; FixSegmentationInNonCSSent.java; etc

Step 6:
Later, Stanford Parser was used to get part-of-speech tags, dependency relation and syntactic governor for all the sentences in the refined corpus. A Chinese 5-gram Wikipedia language model was used to calculate surprisal of each word in each sentence in the refined corpus. The following codes were written to calculate word entropy and other variables of interest.
ConllCSVWriter.java; CalculateEntropy.java; AddEntropy.java; etc

Step 7:
After cleaning the sentences, we wrote the following codes to get a code-switched Database CSV file and a non-code-switched Database CSV file. By matching a similar non-code-switched sentence to a code-switched sentence, we got an input CSV file to run model selection in R.
CSDatabaseWriter.java; NonCSDatabaseWriter.java

**Note:**
This project may contain bugs or errors when processing Chinese-English corpus. Please send us an email if you have any comment or find any problem related to this project. 

