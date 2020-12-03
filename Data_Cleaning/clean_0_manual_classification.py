# -*- coding: utf-8 -*-
import csv, cPickle

'''
This script receives a file with code-switched sentences, with their translations, and some indices, and tries to classify manually each sentence according to the
type of code-switching: clean CS, proper nouns, locations, etc
Essentially it splits the original file into several files that contain the original information
'''


filename='11112018-cmucssa-period-added.csv'
output_directory="outputFiles_CMU"



clean_filename=output_directory+'/11112018-cmucssa-clean.csv'
proper_nouns_only_filename=output_directory+'/11112018-cmucssa-propernouns.csv'
internet_slang_filename=output_directory+'/11112018-cmucssa-internet.csv'
other_filename=output_directory+'/11112018-cmucssa-other.csv'
no_longer_CS_filename=output_directory+'/11112018-cmucssa-nolonger.csv'
alignment_filename=output_directory+'/11112018-cmucssa-alignment.csv'

dictionaries_filename=output_directory+"/11122018-cmucssa-dicts.pick" #save as pickle


    
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
    
    
    
    import os.path
    if os.path.exists(dictionaries_filename):
        input_dictionaries_file=file(dictionaries_filename,'rb')
        all_dictionaries=cPickle.load(input_dictionaries_file)
        input_dictionaries_file.close()
        
        proper_code_switches=all_dictionaries[0]
        company_names=all_dictionaries[1]
        location_names=all_dictionaries[2]
        other_proper_nouns=all_dictionaries[3]
        internet_slang=all_dictionaries[4]
        other_stuff=all_dictionaries[5]
        deleted_rows=all_dictionaries[6]
    else:
        proper_code_switches={}
        company_names={"giant eagle":1}
        location_names={}
        other_proper_nouns={}
        internet_slang={}
        other_stuff={}
        deleted_rows=[]
        all_dictionaries=[proper_code_switches,company_names,location_names,other_proper_nouns,internet_slang,other_stuff,deleted_rows]
    

    clean_output_csv_file=open(clean_filename,mode="w+")
    clean_writer = csv.writer(clean_output_csv_file, delimiter=',', quotechar='"', quoting=csv.QUOTE_MINIMAL)
    
    other_output_csv_file=open(other_filename,mode="w+")
    other_writer = csv.writer(other_output_csv_file, delimiter=',', quotechar='"', quoting=csv.QUOTE_MINIMAL)
    
    nolonger_output_csv_file=open(no_longer_CS_filename, mode="w+")
    nolonger_writer=csv.writer(nolonger_output_csv_file, delimiter=',', quotechar='"', quoting=csv.QUOTE_MINIMAL)
    
    propernouns_output_csv_file=open(proper_nouns_only_filename, mode="w+")
    propernouns_writer=csv.writer(propernouns_output_csv_file, delimiter=',', quotechar='"', quoting=csv.QUOTE_MINIMAL)
    
    internetslang_output_csv_file=open(internet_slang_filename, mode="w+")
    internetslang_writer=csv.writer(internetslang_output_csv_file, delimiter=',', quotechar='"', quoting=csv.QUOTE_MINIMAL)
    
    alignment_output_csv_file=open(alignment_filename, mode="w+")
    alignment_writer=csv.writer(alignment_output_csv_file, delimiter=',', quotechar='"', quoting=csv.QUOTE_MINIMAL)
    
    def close_files():
        clean_output_csv_file.close()
        propernouns_output_csv_file.close()
        
        other_output_csv_file.close()
        nolonger_output_csv_file.close()
        alignment_output_csv_file.close()
        internetslang_output_csv_file.close()
        
        dictionaries_file=file(dictionaries_filename,'wb')
        cPickle.dump(all_dictionaries,dictionaries_file,protocol=cPickle.HIGHEST_PROTOCOL)
        dictionaries_file.close()
    
    with open (filename) as input_csv_file:
        csv_reader = csv.reader(input_csv_file, delimiter=',')
        line_count = 0
        for row in csv_reader:
            if line_count == 0:
                print ", ".join(row)
                line_count += 1
            elif row[0] in deleted_rows:
                nolonger_writer.writerow(row)
                print "no longer a CS, saved to separate file"
            else:
                sentence_id=row[0]
                sentence_original=row[1]
                sentence_tokenized=sentence_original.split()
                sentence_translated=row[2]
                
                indices_CS=row[4][1:-1].split("_")
                if indices_CS==['']:
                    nolonger_writer.writerow(row)
                    continue
                
                indices_CS=[int(index_string) for index_string in indices_CS]
                CS_chunks=get_chunks(sentence_tokenized, indices_CS)
                
                indices_CS_translation=row[5][1:-1].split("_")
                if indices_CS_translation==['']:
                    nolonger_writer.writerow(row)
                    continue
                    
                indices_CS_translation=[int(index_string) for index_string in indices_CS_translation]
                CS_chunks_translation=get_chunks(sentence_translated.split(), indices_CS_translation)
                
                status_CS_words=[]
                chunk_removed=False
                clean=False
                
                for (chunk,indices) in CS_chunks:
                    chunk_type=0
                    if proper_code_switches.has_key(chunk):
                        chunk_type="CS"
                        proper_code_switches[chunk]+=1
                        clean=True
                        
                    elif company_names.has_key(chunk):
                        chunk_type="CN"
                        company_names[chunk]+=1
                    elif location_names.has_key(chunk):
                        chunk_type="LN"
                        location_names[chunk]+=1
                    elif other_proper_nouns.has_key(chunk): 
                        chunk_type="ON"
                        other_proper_nouns[chunk]+=1
                        
                        
                    elif internet_slang.has_key(chunk):
                        chunk_type="IS"
                        internet_slang[chunk]+=1
                    
                    elif other_stuff.has_key(chunk):
                        chunk_type="OT"
                        other_stuff[chunk]+=1
                        
     
                    else: #IF THE CHUNK WAS NOT IN EITHER OF THE PREVIOUS DICTIONARIES
                        print
                        print sentence_id
                        print sentence_original
                        print chunk
     
                        possible_options=["1","2","3","4","5","6","7","8"]
                        print "Code-Switch (1)  Company (2) Location (3) OtherProperNoun (4) Internet-slang (5) RemoveChunk(6) Others(7)  Quit(8)"
                        option=raw_input("Option:")
                        
                        while option not in possible_options:
                            option=raw_input("Option:")
                        
                        
                        if option=="1":
                            chunk_type="CS"
                            proper_code_switches[chunk]=1
                            
                        elif option=="2":
                            chunk_type="CN"
                            company_names[chunk]=1
                        elif option=="3":
                            chunk_type="LN"
                            location_names[chunk]=1
                        elif option=="4":
                            chunk_type="ON"
                            other_proper_nouns[chunk]=1
                        
                        elif option=="5":
                            chunk_type="IS"
                            internet_slang[chunk]=1
                            
                        
                        elif option=="6":
                            if len(CS_chunks)==len(CS_chunks_translation):
                                chunk_index=CS_chunks.index((chunk,indices))
                                (chunk_translated,indices_chunk_translated)=CS_chunks_translation[chunk_index]
                                print 
                                print "Original Chunk:"+chunk
                                print "Translation   :"+chunk_translated
                                option_remove=raw_input( "Confirm? Yes(1) No(2):")
                                if option_remove=="1": 
                                    for index_CS in indices:
                                        indices_CS.remove(index_CS)
                                    for index_CS_translated in indices_chunk_translated:
                                        indices_CS_translation.remove(index_CS_translated)
                                    chunk_removed=True
                                else:
                                    print "Saving to Alignment Problems File"
                                    alignment_writer.writerow(row)
                                    break
                            else:
                                print "Saving to Alignment Problems File"
                                alignment_writer.writerow(row)
                                break
                        
                        elif option=="7":
                            print "saving in others-file"
                            chunk_type="OT"
                            other_stuff[chunk]=1
                
                        
                        elif option=="8":
                            print "QUITTING!!!"
                            close_files()
                            exit()
                        
                            
                            
                    if chunk_removed:
                        row[4]=["_".join(map(str,indices_CS))]
                        row[5]=["_".join(map(str,indices_CS_translation))]
                        
                        if len(indices_CS)==0:
                            nolonger_writer.writerow(row)
                            deleted_rows.append(sentence_id)
                            print "no longer a CS, saved to separate file"
                    if chunk_type:
                        for i in xrange(len(indices)): status_CS_words.append(chunk_type)
                     

                
                line_count += 1
                row.append("_".join(status_CS_words))
                if "OT" in status_CS_words:
                    other_writer.writerow(row)
                elif "CS" in status_CS_words:
                    clean_writer.writerow(row)
                elif "IS" in status_CS_words:
                    internetslang_writer.writerow(row)
                else:
                    propernouns_writer.writerow(row)
                #print "line added to file"+str(line_count)

            
                
        print "processed "+str(line_count)+" lines"
        close_files()

        #print code_switched_words
        #=======================================================================
        # sorted_words=sorted(code_switched_words.iteritems(), key=lambda (k,v): (v,k),reverse=True)
        # for word,count in sorted_words:
        #     print word,count
        #     

        
