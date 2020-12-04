# Surprisal Predicts Code-Switching in Chinese-English Bilingual Text

 This project contains the code to obtain and analyze the data used in the paper: Surprisal Predicts Code-Switching in Chinese-English Bilingual Text presented at the 2020 EMNLP conference.
 
 https://www.aclweb.org/anthology/2020.emnlp-main.330.pdf
 
 If you use the code or data, please include a citation to the paper:
 
@inproceedings{codeswitching2020surprisal,
  title={Surprisal Predicts Code-Switching in Chinese-English Bilingual Text},
  author={Calvillo, Jes{\'u}s and Fang, Le and Cole, Jeremy and Reitter, David},
  booktitle={Proceedings of the 2020 Conference on Empirical Methods in Natural Language Processing (EMNLP)},  
  pages={4029--4039}, 
  year={2020}
}

If you would like to reproduce the statistical analysis, the file Bilingual_v3.R contains the R code that we used. The data used in the analysis is contained in the file 11152019_two_entropies_appended_input_R_v4.csv.

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

Note:
This project may contain bugs and errors when processing Chinese-English corpus. Please send us an email if you have any comment or find any problem related to this project. 

