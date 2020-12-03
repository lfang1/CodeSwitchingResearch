# -*- coding: utf-8 -*-
import csv,numpy

#cs_sentences_filename="in-output-files/02022019_bilingual_database_v1/02022019_bilingual_cs_database_v1.csv"
#non_cs_sentences_filename="in-output-files/02022019_bilingual_database_v1/02022019_bilingual_non_cs_database_v1.csv"
cs_sentences_filename="in-output-files/11152019_database/11152019_bilingual_cs_database_v1.csv"
non_cs_sentences_filename="in-output-files/11152019_database/11152019_bilingual_non_cs_database_v1.csv"

from levSimilarity import levenshtein


def get_dictionary_chinese(dictionary_file_name):
    chinese_dict={}
    with open(dictionary_file_name) as dict_file:
        _ = dict_file.readline()#header file, ignore
        line = dict_file.readline()
        while line:
            elems=line.split("\t")
            word=elems[1].strip()
            freq=float(elems[0])
            chinese_dict[word]=freq
        
            line = dict_file.readline()
    return chinese_dict
    

def levenSimilarity(one,another):
    maxLen=len(one)
    if maxLen<len(another): maxLen=len(another)
    simi = 1.0 - levenshtein(one,another)/float(maxLen)
    return simi 


class Sentence(object):
    def __init__(self,university,s_id,s_type):
        self.university=university
        self.id=s_id
        self.type=s_type
        self.sentence_words=[]
   
    def printMe(self):
        print self.university, self.id,self.type
        for word in self.sentence_words:
                print word
                
    def class_name(self):
        return "Sentence"

class CS_Sentence(Sentence):
        def __init__(self,university,s_id,s_type):
            super(CS_Sentence,self).__init__(university, s_id, s_type)
            self.translation_words=[]
            
        def printMe(self):
            super(CS_Sentence,self).printMe()
            for word in self.translation_words:
                print word
        
        def class_name(self):
            return "CS_Sentence"   
                

def read_CS_sentences(cs_input_filename):    
    with open(cs_input_filename) as cs_input_file:
        csv_reader= csv.DictReader(cs_input_file,delimiter=',')
        cs_sentences=[]
        
        add_original_words=False
        cs_sentence=0
        
        for row in csv_reader:
            if row["source"] != "":
                if cs_sentence !=0 and len(cs_sentence.translation_words)>0:  #if True, it means requirements of a cs sentence have been met and can be added to list
                    cs_sentences.append(cs_sentence)
                
                cs_sentence= CS_Sentence(row["source"],row["sentence id"], row["sentence type"])
                add_original_words=True
                
            elif add_original_words:
                if row["word id"] != "Translation":
                    new_word=(row["word id"], row["word form"], row["cs type"], row["punct switch"], row["freq"])
                    cs_sentence.sentence_words.append(new_word)
                else:add_original_words=False
         
            else:
                new_word=(row["word id"], row["word form"], row["cs type"], row["punct switch"], row["freq"], row["surp"], row["pos tag"], row["head"], row["dep rel"])
                cs_sentence.translation_words.append(new_word)
                
        cs_sentences.append(cs_sentence)
    return cs_sentences

def read_non_CS_sentences(non_cs_input_filename):
    #header: source,sentence id,word id,word form,punct switch,freq,surp,pos tag,head,dep rel
    with open(non_cs_input_filename) as input_file:
        csv_reader= csv.DictReader(input_file,delimiter=',')
        sentences=[]
        sentence=0
        
        for row in csv_reader:
            if row["source"] != "":
                if sentence !=0 and len(sentence.sentence_words)>0: sentences.append(sentence)
                sentence= Sentence(row["source"],row["sentence id"], "non_code_switch")
            else:
                new_word=(row["word id"], row["word form"], row["punct switch"], row["freq"], row["surp"], row["pos tag"], row["head"], row["dep rel"])
                sentence.sentence_words.append(new_word)
                                
        sentences.append(sentence)
    return sentences


def get_chunks(words):
    '''
    Gets subsequences of words, where each subsequence corresponds to continuous indices
    '''
    possible=[]
    for word in words:
        if word[2]!="0":possible.append(word)

    last_CS_index=-2
    CS_chunks=[]
    current_CS_chunk=[]
    
    for current_word in possible:
        if int(current_word[0])-last_CS_index>1:
            if len(current_CS_chunk)>0:CS_chunks.append(current_CS_chunk)
            current_CS_chunk=[current_word]
        else:
            current_CS_chunk.append(current_word)
        last_CS_index=int(current_word[0])
    
    CS_chunks.append(current_CS_chunk)
    return CS_chunks

def get_firstCS_chunk(cs_sentence):
    ch_original=get_chunks(cs_sentence.sentence_words)
    ch_translation=get_chunks(cs_sentence.translation_words)
    
    if len(ch_original)!=len(ch_translation):
        #print "Different number of chunks in original and translation, avoiding mapping"
        return (0,"diff_chunks"),(0,0)
    
    def locate_first_CS_chunk(chunks,initial_index=0):
        #Get the first chunk with a CS
        first_chunk=chunks[initial_index]
        chunk_index=initial_index
        #print first_chunk
        #"CS" is in the originals, "1" is in the translations
        while first_chunk[0][2]!="CS" and chunk_index+1<len(chunks):
            chunk_index+=1
            first_chunk=chunks[chunk_index]
            
        if first_chunk[0][2]!="CS":
            #===================================================================
            # print
            # print first_chunk
            # print "no CS chunk found!!!"
            # sentence_w=" ".join([word[1] for word in cs_sentence.sentence_words])
            # trans_w=" ".join([word[1] for word in cs_sentence.translation_words])
            # print sentence_w
            # print trans_w
            #===================================================================
            return -1,-1
        
        return chunk_index,first_chunk
    
    def locate_first_CS_word(first_ch):
        #Get the first word of the first chunk with a CS
        first_word=first_ch[0]
        word_index=0
        while first_word[3]=="1" and word_index<(len(first_ch)-1):#while it's a punctuation
            print "entered here(first chunk word is punctuation???)"
            word_index+=1
            first_word=first_ch[word_index]
        return first_word

    chunk_index_ori,first_chunk_ori=locate_first_CS_chunk(ch_original)    
    if chunk_index_ori==-1:return (0,"no_cs_chunk"),(0,0)
    first_CS_word_ori=locate_first_CS_word(first_chunk_ori)    
    
    while first_CS_word_ori[3]=='1':
        chunk_index_ori,first_chunk=locate_first_CS_chunk(ch_original,chunk_index_ori+1)
        first_CS_word_ori=locate_first_CS_word(first_chunk)
      
    
    first_chunk_translation=ch_translation[chunk_index_ori]
    first_word_tr=first_chunk_translation[0]
    trans_index=0
    while first_word_tr[3]=="1": #while it's a punctuation
        trans_index+=1
        first_word_tr=first_chunk_translation[trans_index]

    return (first_chunk_ori,first_CS_word_ori),(first_chunk_translation,first_word_tr)



def get_location_posneeded(cs_sent,first_word_tr):
    if first_word_tr[0]=="1": #if the CS-word is the first one in the sentence
        next_word=cs_sent.translation_words[1]
        pos_needed=first_word_tr[6]+"_"+next_word[6]
        location="BEG"
    elif first_word_tr[0]==str(len(cs_sent.translation_words)):#if it's the last word in the sentence
        previous_word=cs_sent.translation_words[int(first_word_tr[0])-2]
        pos_needed=previous_word[6]+"_"+first_word_tr[6]
        location="END"
    else: #if it's in the middle
        previous_word=cs_sent.translation_words[int(first_word_tr[0])-2]
        next_word=cs_sent.translation_words[int(first_word_tr[0])]
        pos_needed=previous_word[6]+"_"+first_word_tr[6]+"_"+next_word[6]
        location="MID"  
    return location,pos_needed
    

def mostSimilarEquivalentsLevens(postags_sent,list_sents,n=20):
    
    
    similarities=[levenSimilarity(postags_cs,sent2[0]) for sent2 in list_sents]        
    top_idx = numpy.argsort(similarities)[-n:]
    top_values = [similarities[i] for i in top_idx]

    result=[]
    for index_sim, value in zip(top_idx,top_values):
        result.append((value,list_sents[index_sim]))
     
    return sorted(result, key=lambda tup: tup[0], reverse=True)


#headers= "source,sentence id,sentence type,word id,word form,cs type,punct switch,freq,surp,pos tag,head,dep rel"

def get_match_list_index(sublist,big_list):
    '''
    Returns the indices (each from 1, to n) of the locations where big_list matches the sublist 
    '''
    import re
    word_indices=[]
    
    big_list_string= "_".join(big_list)
    sublist_string="_".join(sublist)
    indices=[m.start() for m in re.finditer(sublist_string, big_list_string)]
    for ind in indices:
        prefix=big_list_string[:ind]
        slices=prefix.split("_")
        word_indices.append(len(slices))
    return word_indices

def get_possible_cspoints(pos_needed,location,candidates):
    new_candidates=[]
    for (sim,candidate) in candidates:
        postags_candidate=candidate[0]
        noncs_sent=candidate[1]

        possible_cspoint_indices_candidate=get_match_list_index(pos_needed.split("_"), postags_candidate)
        if location!="BEG":possible_cspoint_indices_candidate=[pos_indices+1 for pos_indices in possible_cspoint_indices_candidate]
        new_candidates.append((sim,postags_candidate,noncs_sent,possible_cspoint_indices_candidate))
    return new_candidates


def verify_nonwords(candidates,chinese_dictionary):
    new_candidates=[]
    for (sim,postags_candidate,noncs_sent,possible_cs_points) in candidates:
        possible_cs_points=[pos_index for pos_index in possible_cs_points if noncs_sent.sentence_words[pos_index-1][1] in chinese_dictionary.keys()]
        if len(possible_cs_points)>0:new_candidates.append((sim,postags_candidate,noncs_sent,possible_cs_points))
    return new_candidates

def get_best_ones(candidates):
    best_ones=[candidates[0]]
    best_sim=best_ones[0][0]
    for (sim,postags_candidate,noncs_sent,possible_cs_points) in candidates[1:]:
        if sim<best_sim:break
        best_ones.append((sim,postags_candidate,noncs_sent,possible_cs_points))
    return best_ones

def verify_dependency(dependency,candidates):
    new_candidates=[]
    for (sim,postags_candidate,noncs_sent,possible_cs_points) in candidates:
        possible_cs_points=[pos_cspoint for pos_cspoint in possible_cs_points if noncs_sent.sentence_words[pos_cspoint-1][7]==dependency]
        if len(possible_cs_points)>0:new_candidates.append((sim,postags_candidate,noncs_sent,possible_cs_points))
    return new_candidates

def verify_location_cspoint(location,candidates_cspoint):
    new_candidates_cspoint=[]
    if location=="BEG" or location=="END":
        for (sim,postags_candidate,noncs_sent,possible_cs_points) in candidates_cspoint:            
            if location=="BEG":
                first_point=possible_cs_points[0]
                possible_cs_points=[first_point]
 
            if location=="END":
                last_point=possible_cs_points[-1]
                possible_cs_points=[last_point]
            new_candidates_cspoint.append((sim,postags_candidate,noncs_sent,possible_cs_points))
        
        return new_candidates_cspoint
    else: return candidates_cspoint
    
def get_surprisal_values(sentence):
    if sentence.class_name() =="CS_Sentence":
        sent_words=sentence.translation_words
    elif sentence.class_name()=="Sentence":
        sent_words=sentence.sentence_words
    
    surprisal_values=[]
    for word in sent_words:
        if sentence.class_name() =="CS_Sentence":
            surprisal_values.append(word[5])
        elif sentence.class_name() =="Sentence":
            surprisal_values.append(word[4])
        
    return surprisal_values

def add_cs_sent_row(cs_sentence,ncs_sent,csv_writer):  
    (_,first_word_orig),(_,first_word_tr)=get_firstCS_chunk(cs_sentence)
    first_cs_trans_index=first_word_tr[0]
    first_cs_trans_word=first_word_tr[1]
    first_cs_trans_freq=first_word_tr[4]
    first_cs_trans_surp=first_word_tr[5]
    first_cs_trans_postag=first_word_tr[6]
    first_cs_trans_governor=first_word_tr[7]
    first_cs_trans_deprel=first_word_tr[8]
    
    surprisals=" ".join(get_surprisal_values(cs_sent))
    original_tokenized=" ".join([word for (s_id,word,cs_type,punct,freq) in cs_sentence.sentence_words])
    translation_tokenized=" ".join([word for (s_id,word,cs_type,punct,freq,surp,tag,head,deprel) in cs_sentence.translation_words])
    
    aligned_to=ncs_sent.university+"_"+ncs_sent.id
    
    row=["code-switch",cs_sentence.university,cs_sentence.id,aligned_to,original_tokenized,translation_tokenized,first_cs_trans_index,first_word_orig[1],
         first_cs_trans_word,first_cs_trans_freq,first_cs_trans_surp,first_cs_trans_postag,first_cs_trans_governor,first_cs_trans_deprel,surprisals] 
    csv_writer.writerow(row)
        
def add_non_cs_row(non_cs_sentence,cs_point,cs_sent,csv_writer):
    word_ncs_aligned=non_cs_sentence.sentence_words[cs_point-1]
                   
    first_word_cs_aligned=""
    first_ncs_word=word_ncs_aligned[1]
    first_ncs_freq=word_ncs_aligned[3]
    first_ncs_surp=word_ncs_aligned[4]
    first_ncs_postag=word_ncs_aligned[5]
    first_ncs_governor=word_ncs_aligned[6]
    first_ncs_deprel=word_ncs_aligned[7]
    
    surprisals=" ".join(get_surprisal_values(non_cs_sentence))
    sentence_tokenized=" ".join([word for (s_id,word,punct,freq,surp,tag,head,deprel) in non_cs_sentence.sentence_words])
    aligned_to=cs_sent.university+"_"+cs_sent.id
    
    row=["non-code-switch",non_cs_sentence.university,non_cs_sentence.id,aligned_to," ",sentence_tokenized,cs_point,first_word_cs_aligned,
         first_ncs_word,first_ncs_freq,first_ncs_surp,first_ncs_postag,first_ncs_governor,first_ncs_deprel,surprisals]
    csv_writer.writerow(row)
       
if __name__ == '__main__':  
    cs_sentences=read_CS_sentences(cs_sentences_filename)   
    non_cs_sentences=read_non_CS_sentences(non_cs_sentences_filename)
    
    chinese_dictionary=get_dictionary_chinese("55k-chinese-word-independent-probabilities.txt")
    
    output_file=open("in-output-files/r_files/input_R_v4_new_15_11_2019.csv",mode="w+")
    writer=csv.writer(output_file,delimiter=',', quotechar='"', quoting=csv.QUOTE_MINIMAL)  
    headers=["sent_type","university","id","aligned_to","original_sentence","translation","index_CS","first_cs_word","first_cs_word_translation","frequency_first_word_trans","surprisal_first_word_trans","postag_first_word_trans","governor_first_word_trans","deprel_first_word_trans","surprisal_values"]
    writer.writerow(headers)
    
    universities=["CMU","PSU","PIT"]#
    global_max_surp=0
    for uni in universities:
    
        cs_sents_uni=[sent for sent in cs_sentences if sent.university==uni and sent.type=="clean_code_switch"]
        non_cs_sents_uni=[sent for sent in non_cs_sentences if sent.university==uni]    
               
        csid_2_cssent={sent.university+"_"+sent.id:sent for sent in cs_sents_uni}    
        ncsid_2_ncssent={sent.university+"_"+sent.id:sent for sent in non_cs_sents_uni}
        
        available_non_cs_sents=[sent.university+"_"+sent.id for sent in non_cs_sents_uni]
        used_non_cs_sents=[]
        
        noncs_postags=[]    
        for non_cs_sent_id in available_non_cs_sents:
            non_cs_sent= ncsid_2_ncssent[non_cs_sent_id]
            postags=[tag for (s_id,word,punct,freq,surp,tag,head,deprel) in non_cs_sent.sentence_words]
            noncs_postags.append((postags,non_cs_sent))
                    
        no_cs_chunk=[]
        diff_chunk_lengths=[]
        no_ngram=[]
        not_enough_sim=[]
        no_words_at_cspoint=[]
        distance_needed=[]
        removed_cs_sents_freq=[]
        dependency_not_found=[]
        min_sim_threshold=0.4
        print uni
        print "initial cs_sents",len(cs_sents_uni)
        print "initial non_cs_sents:",len(available_non_cs_sents) 
        
        for cs_sent in cs_sents_uni:
            
            (first_chunk,first_word),(first_translation,first_word_tr) = get_firstCS_chunk(cs_sent)
            if first_word=="no_cs_chunk":
                no_cs_chunk.append(cs_sent)
                continue
            if first_word=="diff_chunks":
                diff_chunk_lengths.append(cs_sent)
                continue
            
            first_cs_trans_freq=first_word_tr[4]
            if first_cs_trans_freq=="":
                removed_cs_sents_freq.append(cs_sent.university+"_"+cs_sent.id)
                continue
            
            
            location,pos_need=get_location_posneeded(cs_sent,first_word_tr)
            postags_cs=[tag for (s_id,word,cstype,punct,freq,surp,tag,head,deprel) in cs_sent.translation_words]
    
            candidates_postags=[noncs_sent for noncs_sent in noncs_postags if pos_need in "_".join(noncs_sent[0])]
            
            if len(candidates_postags)==0:
                no_ngram.append(cs_sent)
                continue
            
            top20=mostSimilarEquivalentsLevens(postags_cs, candidates_postags) #sort and get only the top 20 candidates according to similarity of their postags
            top20=[(sim,candidate) for (sim,candidate) in top20 if sim>=min_sim_threshold] # remove all candidates that don't have the minimum similarity 
            if len(top20)==0:
                not_enough_sim.append(cs_sent)
                continue
           
            #(sim,postags_candidate,noncs_sent,possible_cs_points)
            candidates_cspoints=get_possible_cspoints(pos_need, location, top20)
            #Verify that the words at the possible CS-ppint are not punctuations or OOV words
            word_verified_candidates=verify_nonwords(candidates_cspoints,chinese_dictionary)
            if len(word_verified_candidates)==0:
                no_words_at_cspoint.append(cs_sent)
                continue
            
            dependency=first_word_tr[8]
            dependency_verified=verify_dependency(dependency,word_verified_candidates)
            if len(dependency_verified)>0:best_ones_sofar=get_best_ones(dependency_verified)
            else:
                best_ones_sofar=get_best_ones(word_verified_candidates)#print "dependency not found"
                dependency_not_found.append(cs_sent)
            best_ones_location=verify_location_cspoint(location, best_ones_sofar)
            non_cs_selected=best_ones_location[0]   
            
            #If there is one candidate with a single possible cspoint, we select that
            for non_cs in best_ones_location:
                if len(non_cs[3])==1:
                    non_cs_selected=non_cs
                    break
            #otherwise, we pick the first candidate and select the cspoint as the one with the lowest distance to the original cspoint in the translation
            #a more elegant solution would be through sequence alignment: https://www.geeksforgeeks.org/sequence-alignment-problem/
            #but because they are so few that apply the distance heuristic, we can just use the distance, as it's not bad either
            if len(non_cs_selected[3])>1:
                distance_needed.append(cs_sent)
                distances_cspoints=[abs(possible_cspoint-int(first_word_tr[0])) for possible_cspoint in non_cs_selected[3]]
                index_lowest_distance=numpy.argsort(distances_cspoints)[0]
                non_cs_selected=list(non_cs_selected)
                non_cs_selected[3]=[non_cs_selected[3][index_lowest_distance]]
                non_cs_selected=tuple(non_cs_selected)
                  
            non_cs_id=non_cs_selected[2].university+"_"+non_cs_selected[2].id
            available_non_cs_sents.remove(non_cs_id)
            used_non_cs_sents.append(non_cs_id)
            noncs_postags.remove((non_cs_selected[1],non_cs_selected[2]))
            
            add_cs_sent_row(cs_sent, non_cs_selected[2], writer)
            add_non_cs_row(non_cs_selected[2], non_cs_selected[3][0], cs_sent, writer)



        print "used cs_sents/noncs_sents:",len(used_non_cs_sents)
        print        
        print "removed because original had no CS chunk:", len(no_cs_chunk)
        print "removed because original and translation had different number of chunks:",len(diff_chunk_lengths)
        print "removed due to postag pattern misssing:",len(no_ngram)
        print "removed due to not enough similarity:",len(not_enough_sim)
        print "removed because no word at cspoint:",len(no_words_at_cspoint)
        print "removed cs_sents due to null freq at cspoint:",len(removed_cs_sents_freq)
        print "distance heuristic needed for cspoint:",len(distance_needed)
        print "dependency not found:",len(dependency_not_found)
        print
        print "not used noncs_sents:",len(available_non_cs_sents) 
        
        print
        print
        
    output_file.close()    
