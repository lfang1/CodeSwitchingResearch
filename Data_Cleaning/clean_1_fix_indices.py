# -*- coding: utf-8 -*-
'''
After separating the code-switched sentences by clean_0_manual_classification, this file fixes an issue with the indices
It tries to recompute and/or fix errors in the punctuation and CS indices of each row in the files
'''

import csv

input_directory="outputFiles_PSU"
file_prefix=input_directory+"/11112018-psucssa"

clean_filename=file_prefix+'-clean'
proper_nouns_only_filename=file_prefix+'-propernouns'
internet_slang_filename=file_prefix+'-internet'
other_filename=file_prefix+'-other'
no_longer_CS_filename=file_prefix+'-nolonger'
alignment_filename=file_prefix+'-alignment'
files_to_process=[clean_filename,proper_nouns_only_filename,internet_slang_filename,other_filename,no_longer_CS_filename,alignment_filename]
    
def get_chunks(sentence_tokenized,CS_indices):
    last_CS_index=-2
    CS_chunks=[]
    current_CS_chunk=0
    current_chunk_indices=0
    
    for current_CS_index in CS_indices:
        if current_CS_index-last_CS_index>1:
            if current_CS_chunk:
                CS_chunks.append((" ".join(current_CS_chunk),current_chunk_indices))
            
            current_CS_chunk=[sentence_tokenized[current_CS_index]]
            current_chunk_indices=[current_CS_index]
        else:
            current_CS_chunk.append(sentence_tokenized[current_CS_index])
            current_chunk_indices.append(current_CS_index)
        last_CS_index=current_CS_index
    
    CS_chunks.append((" ".join(current_CS_chunk),current_chunk_indices))
    return CS_chunks


if __name__ == '__main__':    
  
    from string import punctuation
    english_punctuation=[c for c in punctuation]
    chinese_punctuation=["？","，",",","！","；","：","（","）","［","］","【","】","。","「","」","﹁","﹂","“","”","‘","’","、","‧","《","》","〈","〉","……","——","—","～","__","﹏﹏","·", "•"]

    for current_filename in files_to_process:
        current_output_file=open(current_filename+"_ind_fixed.csv",mode="w+")
        current_writer=csv.writer(current_output_file,delimiter=',', quotechar='"', quoting=csv.QUOTE_MINIMAL)
        
        with open(current_filename+".csv") as current_input_file:
            csv_reader= csv.reader(current_input_file,delimiter=',')
            
            for row in csv_reader:
                row=[elem for elem in row if elem]
                
                sentence_id=row[0]
                original_sentence=row[1]
                original_tokenized=original_sentence.split()
                translation=row[2]
                translation_tokenized=translation.split()
                
                #FIND INDICES OF PUNCTUATION TOKENS
                punct_original=[]
                punct_translation=[]
                for token_index in xrange(len(original_tokenized)):
                    current_token=original_tokenized[token_index]
                    if current_token in chinese_punctuation or current_token in english_punctuation:
                        punct_original.append(token_index)
                
                for token_index in xrange(len(translation_tokenized)):
                    current_token=translation_tokenized[token_index]
                    if current_token in chinese_punctuation or current_token in english_punctuation:
                        punct_translation.append(token_index)
                
                #FIX CASES WHERE A LINE HAVE LESS ITEMS THAN NORMAL AND ASSIGN PUNCTUATION INDICES
                if len(row) is not 9:
                    cs_type=row[-1]
                    if len(row)==7:
                        row[6]="["+"_".join([str(elem) for elem in punct_original])+"]"
                        row.append("["+"_".join([str(elem) for elem in punct_translation])+"]")
                        row.append(cs_type)
                    if len(row)==8:
                        row[6]="["+"_".join([str(elem) for elem in punct_original])+"]"
                        row[7]="["+"_".join([str(elem) for elem in punct_translation])+"]"
                        row.append(cs_type)
                else:
                    
                    row[6]="["+"_".join([str(elem) for elem in punct_original])+"]"
                    row[7]="["+"_".join([str(elem) for elem in punct_translation])+"]"
                
                
                cs_indices=row[4].replace("'", '')
                if cs_indices== "[]":
                    print "error: empty cs_indices"
                    continue 
                cs_indices=cs_indices[1:-1].split("_")
                cs_indices=[int(index_s) for index_s in cs_indices]
                
                translation_cs_indices=row[5].replace("'", '')
                if translation_cs_indices== "[]":
                    print "error: empty translation_cs_indices"
                    continue 
                translation_cs_indices=translation_cs_indices[1:-1].split("_")
                translation_cs_indices=[int(index_s) for index_s in translation_cs_indices]
                
                #ATTACH PUNCTUATION TO CODE-SWITCHED CHUNKS, IF THE PUNCTUATION FOLLOWS THE CHUNK
                original_chunks=get_chunks(original_tokenized, cs_indices)
                translation_chunks=get_chunks(translation_tokenized,translation_cs_indices)
                
                for chunk,chunk_indices in translation_chunks:
                    if chunk_indices[-1]+1 in punct_translation:
                        translation_cs_indices.append(chunk_indices[-1]+1)
                        translation_cs_indices.sort()
               
                row[4]="["+"_".join([str(i) for i in cs_indices])+"]"
                row[5]="["+"_".join([str(i) for i in translation_cs_indices])+"]"
                
                current_writer.writerow(row)
        current_output_file.close()
    