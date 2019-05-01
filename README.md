# CodeSwitchingResearch
  This project contains all the important codes for the code-switching research.

  First, I collected raw Chinese-English corpus by scraping text from Chinese Students and Scholars Association Bulletin Board System (CSSA BBS) of three universities, which are Pennsylvanina State University, Carnegie Mellon University, and University of Pittsburgh.

  After obtaining the raw corpus, I used Stanford Segementer to tokenize the words in the sentences.
In order to identify code-switching in the corpus, I built a Chinese Dictionary and an English Dictionary based on Google 1-gram corpus. By refering to the two dictionaries, I could distinguish if a word is English or Chinese and get its relative frequency.

  I combined all the sentences that contain code-switching into a code-switched corpus. Then I hired four international Chinese undergraduate students to translated the code-switched corpus into Chinese. The translation contains both word-by-word translation and the whole-sentence translation.

  After I recevied the translation, I wrote some codes to get a refined corpus by cleaning and merging the translated sentences with the original sentences, and getting location indices of each code-switched word and punctuation in the translated sentences. 

  Later, I used Stanford Parser to get part-of-speech tags, dependency relation and syntactic governor for all the sentences in the refined corpus. I also used a Chinese 5-gram Wikipedia language model to calcualte surprisal of each word in each sentence in the refined corpus.
After cleaning the sentences, I got a code-switched Database CSV file and a non-code-switched Database CSV file. By matching a similar non-code-switched sentence to a code-switched sentence, I got an input CSV file to run model selection in R.
