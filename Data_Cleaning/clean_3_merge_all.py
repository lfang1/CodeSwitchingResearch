# -*- coding: utf-8 -*-
'''
Merges all clean and proper nouns for all universities
does the same for the other cases
'''

import csv
universities=["PSU","CMU","PIT"]
    
if __name__ == '__main__':    
    
    all_clean_and_pns_filename="all_clean_and_pns.csv"
    all_others_filename="all_others.csv"
    
    all_cleans_file=open(all_clean_and_pns_filename,mode="w+")
    cleans_writer=csv.writer(all_cleans_file,delimiter=",", quotechar='"', quoting=csv.QUOTE_MINIMAL)
    
    all_others_file=open(all_others_filename, mode="w+")
    others_writer=csv.writer(all_others_file,delimiter=",", quotechar='"', quoting=csv.QUOTE_MINIMAL)
    
    for university in universities:
        current_input_directory="outputFiles_"+university+"/"
        current_clean_file=current_input_directory+university+"_clean_and_propernouns.csv"
        current_others_file=current_input_directory+university+"_other_cases.csv"

   
        with open(current_clean_file) as current_input_file:
            csv_reader= csv.reader(current_input_file,delimiter=',')
            for row in csv_reader:
                row.append(university)
                cleans_writer.writerow(row)
                
        with open(current_others_file) as current_input_file:
            csv_reader= csv.reader(current_input_file,delimiter=',')
            for row in csv_reader:
                row.append(university)
                others_writer.writerow(row)
                
    all_cleans_file.close()
    all_others_file.close()            