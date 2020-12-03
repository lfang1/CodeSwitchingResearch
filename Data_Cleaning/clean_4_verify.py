# -*- coding: utf-8 -*-
'''
Very similar to clean_1_fix_indices
Here we verify a couple of things of the clean files
-FIND INDICES OF PUNCTUATION TOKENS
-VERIFIES THAT THE TRANSLATION INDICES DO NOT OVERPASS THE LARGEST INDEX (??)
-ADDS PUNCTUATIONS TO CS CHUNKS IF THE PUNCTUATION TOKEN FOLLOWS THE CHUNK
-VERIFY THAT THE NUMBER OF CS TOKENS IS EQUAL TO THE LABELS OF THE TYPES OF CS
-VERIFY THAT THE CS TYPES ARE PERMITTED
'''

import csv
    
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
    input_filenames=["in-output-files/11132019_all_clean_and_pns_ver"]
  
    from string import punctuation    
    english_punctuation=[c for c in punctuation]
    chinese_punctuation=["？","，",",","！","；","：","（","）","［","］","【","】","。","「","」","﹁","﹂","“","”","‘","’","、","‧","《","》","〈","〉","……","——","—","～","__","﹏﹏","·", "•"]

    no_cs_indices=[]
    no_trans_cs_indices=[]
    diff_punctuation_original=[]
    diff_punctuation_translation=[]
    punctuation_initial_as_cs=[]

    for current_filename in input_filenames:
        current_output_file=open(current_filename+"_new.csv",mode="w+")
        current_writer=csv.writer(current_output_file,delimiter=',', quotechar='"', quoting=csv.QUOTE_MINIMAL)
        
        with open(current_filename+".csv") as current_input_file:
            csv_reader= csv.reader(current_input_file,delimiter=',')
            first_row=next(csv_reader)
            
            for row in csv_reader:               
                row=[elem for elem in row if elem]
                
                sentence_id=row[0]
                original_tokenized=row[1].split()
                translation_tokenized=row[2].split()
                
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
                
                punct_original_string="["+"_".join([str(elem) for elem in punct_original])+"]"
                if row[6]!=punct_original_string:
                    diff_punctuation_original.append(row)
                    row[6]=punct_original_string
                    #===========================================================
                    # print row[1]
                    # print row[6]
                    # print punct_original_string
                    # print "punctuation_original not equal to new"
                    #===========================================================
                
                punct_translation_string="["+"_".join([str(elem) for elem in punct_translation])+"]"
                if row[7]!= punct_translation_string:
                    diff_punctuation_translation.append(row)
                    row[7]=punct_translation_string
                    #===========================================================
                    # print row[2]
                    # print row[7]
                    # print punct_translation_string
                    # print "punctuation in translation different from new" 
                    #===========================================================
                
                cs_indices=row[4]
                if cs_indices=="[]":
                    #print "no CS indices!"
                    #print row[1]
                    no_cs_indices.append(row)
                    continue
                cs_indices=cs_indices[1:-1].split("_")
                cs_indices=[int(index_s) for index_s in cs_indices]
                
                translation_cs_indices=row[5].replace("'", '')
                if translation_cs_indices=="[]":
                    #print "no translation cs indices!"
                    if "CS" in row[8]:
                        #print row[0],row[10],row[2]
                        #print row[8]
                        no_trans_cs_indices.append(row)
                    continue
                translation_cs_indices=translation_cs_indices[1:-1].split("_")
                translation_cs_indices=[int(index_s) for index_s in translation_cs_indices]
                
                #VERIFIES THAT THE TRANSLATION INDICES DO NOT OVERPASS THE LARGEST INDEX (??)
                for trans_cs_index in translation_cs_indices:
                    if trans_cs_index>len(translation_tokenized):
                        print "error: translation index larger than number of tokens in translation"
                
                
                #ADDS PUNCTUATIONS TO CS CHUNKS IF THE PUNCTUATION TOKEN FOLLOWS THE CHUNK
                #REMOVES THEM IF THEY ARE AT THE BEGINNING OF THE CHUNK
                original_chunks=get_chunks(original_tokenized, cs_indices)
                translation_chunks=get_chunks(translation_tokenized,translation_cs_indices)
                #print row[2]
                for chunk,chunk_indices in translation_chunks:
                    x_offset=1
                    while chunk_indices[-1]+x_offset in punct_translation and chunk_indices[-1]+x_offset not in translation_cs_indices:
                        #=======================================================
                        # print "Last word plus 1 is punctuation"
                        # print row[1]
                        # print row[2]
                        # print chunk_indices
                        # print "cs indices:",cs_indices
                        # print "trans inds before:",translation_cs_indices
                        #=======================================================
                        
                        translation_cs_indices.append(chunk_indices[-1]+x_offset)
                        translation_cs_indices.sort()
                        x_offset+=1
                        #=======================================================
                        # print "trans inds after:",translation_cs_indices
                        # print
                        #=======================================================
                    
                    x_offset=0
                    while x_offset<len(chunk_indices) and chunk_indices[0+x_offset] in punct_translation:
                        print "here2"
                        #=======================================================
                        # print "First word is punctuation"
                        # print row[1]
                        # print row[2]
                        # print chunk_indices
                        # print "cs indices:",cs_indices
                        # print "trans inds before:",translation_cs_indices
                        #=======================================================
                        translation_cs_indices.remove(chunk_indices[0+x_offset])
                        x_offset+=1
                        #=======================================================
                        # print "trans inds after:",translation_cs_indices
                        # print
                        #=======================================================
                        
                for chunk,chunk_indices in original_chunks:
                    x_offset=1
                    while chunk_indices[-1]+x_offset in punct_original and chunk_indices[-1]+x_offset not in cs_indices:
                        print "here3"
                        #=======================================================
                        # print "Last word plus 1 is punctuation"
                        # print row[1]
                        # print row[2]
                        # print chunk_indices
                        # print "trans inds:",translation_cs_indices
                        # print "cs indices before:",cs_indices
                        #=======================================================
                        cs_indices.append(chunk_indices[-1]+x_offset)
                        cs_indices.sort()
                        x_offset+=1
                        #=======================================================
                        # print "cs indices after:",cs_indices
                        # print
                        #=======================================================
                        
                    x_offset=0
                    while x_offset<len(chunk_indices) and chunk_indices[0+x_offset] in punct_original:
                        print "here4"
                        #=======================================================
                        # print "First word is punctuation"
                        # print row[1]
                        # print row[2]
                        # print chunk_indices
                        # print "trans inds:",translation_cs_indices
                        # print "cs indices before:",cs_indices
                        #=======================================================
                        cs_indices.remove(chunk_indices[0+x_offset])
                        x_offset+=1
                        #=======================================================
                        # print "cs indices after:",cs_indices
                        # print
                        #=======================================================
               
                    
                row[4]="["+"_".join([str(i) for i in cs_indices])+"]"
                row[5]="["+"_".join([str(i) for i in translation_cs_indices])+"]"
                
                
                #===============================================================
                # #VERIFY THAT THE NUMBER OF CS TOKENS IS EQUAL TO THE LABELS OF THE TYPES OF CS
                # permitted_cs_types=["CS","IS","CN","ON","LN","OT","AL","NO"]
                # cs_types=row[8].split("_")
                # if len(cs_types)!=len(cs_indices):
                #       
                #     print row[1]
                #     print sentence_id, row[10]
                #     print cs_types
                #     print cs_indices
                #     print
                # continue
                #===============================================================
                 
                #===============================================================
                # #VERIFY THAT THE CS TYPES ARE PERMITTED
                # for cs in cs_types:
                #     if cs not in permitted_cs_types:
                #         print row 
                #===============================================================
                        
                current_writer.writerow(row)
        current_output_file.close()
        print len(no_cs_indices)
        print len(no_trans_cs_indices)
        print len(diff_punctuation_original)
        print len(diff_punctuation_translation)
        print len(punctuation_initial_as_cs)
