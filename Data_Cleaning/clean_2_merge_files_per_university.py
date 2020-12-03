# -*- coding: utf-8 -*-
'''
After fixing the indices by clean_1_fix_indices, this file takes a directory that contains all files for a given university
and separates those that are interesting: proper nouns and clean code-switches from the rest
Then it merges all files of each category into one file (so 2 files at the end, one for interesting, and one for the rest)
The 2 files are named:
- input_directory+"/"+university+"_clean_and_propernouns.csv"
- input_directory+"/"+university+"_other_cases.csv"

'''

import csv
university="PSU"

input_directory="outputFiles_"+university
file_prefix=input_directory+"/11112018-psucssa"

clean_filename=file_prefix+'-clean_ind_fixed'
proper_nouns_only_filename=file_prefix+'-propernouns_ind_fixed'
internet_slang_filename=file_prefix+'-internet_ind_fixed'
other_filename=file_prefix+'-other_ind_fixed'
no_longer_CS_filename=file_prefix+'-nolonger_ind_fixed'
alignment_filename=file_prefix+'-alignment_ind_fixed'


good_files=[clean_filename,proper_nouns_only_filename]
bad_files=[internet_slang_filename,other_filename,no_longer_CS_filename,alignment_filename]

    
if __name__ == '__main__':    

    good_output_file=open(input_directory+"/"+university+"_clean_and_propernouns.csv",mode="w+")
    good_writer=csv.writer(good_output_file,delimiter=',', quotechar='"', quoting=csv.QUOTE_MINIMAL)
    
    for current_input_filename in good_files:
        with open(current_input_filename+".csv") as current_input_file:
            csv_reader= csv.reader(current_input_file,delimiter=',')
            for row in csv_reader:
                if current_input_filename==file_prefix+'-clean_ind_fixed':
                    row.append("clean_code_switch")
                if current_input_filename==file_prefix+'-propernouns_ind_fixed':
                    row.append("proper_nouns")
                good_writer.writerow(row)
    good_output_file.close()
    
    
    bad_output_file=open(input_directory+"/"+university+"_other_cases.csv",mode="w+")
    bad_writer=csv.writer(bad_output_file,delimiter=',', quotechar='"', quoting=csv.QUOTE_MINIMAL)
    for current_input_filename in bad_files:
        with open(current_input_filename+".csv") as current_input_file:
            csv_reader= csv.reader(current_input_file,delimiter=',')
            for row in csv_reader:
                if current_input_filename==file_prefix+'-internet_ind_fixed':
                    row.append("internet_slang")
                if current_input_filename==file_prefix+'-other_ind_fixed':
                    row.append("other")
                if current_input_filename==file_prefix+'-nolonger_ind_fixed':
                    row.append("no_code_switch")
                if current_input_filename==file_prefix+'-alignment_ind_fixed':
                    row.append("alignment_issue")

                bad_writer.writerow(row)
    bad_output_file.close()
            