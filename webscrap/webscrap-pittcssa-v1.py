# -*- coding: utf-8 -*-
import urllib
from bs4 import BeautifulSoup
from urlparse import  urljoin

urlList = []

for i in range(1,45):
	#second hand market
	url = 'http://www.pittcssa.net/bbs2015/forum.php?mod=forumdisplay&fid=2&page=' + str(i)
	urlList.append(url)
	
for i in range(1,84):	
	#house rent:
	url = 'http://www.pittcssa.net/bbs2015/forum.php?mod=forumdisplay&fid=37&page=' + str(i)
	urlList.append(url)

for i in range(1,5):	
	#chat:
	url = 'http://www.pittcssa.net/bbs2015/forum.php?mod=forumdisplay&fid=38&page=' + str(i)
	urlList.append(url)	

for item in urlList:
	print(item)
	html = urllib.urlopen(item).read()
	soup = BeautifulSoup(html, "lxml")
	
	# Remove bottom links
	last_links = soup.find(id='autopbn')
	last_links.decompose()
	
	# Pull all the text from the s xst div
	post_title = soup.find_all(class_='s xst')
	print(post_title)
	
	for title in post_title:
		url = urljoin(item, title.get('href'))
		print("Scraping url: " + url)
		
		post_html = urllib.urlopen(url).read()
		post_soup = BeautifulSoup(post_html, "lxml")
		
		# Remove bottom links
		last_messages = post_soup.find(class_='pgs mtm mbm cl')
		last_messages.decompose();
		
		#Remove mulitple tag: 
		#eg: 
		#for tag in ['ignore_js_op,'blockquote', 'img', ... ]:
		#xxxxsoup.find(tag).decompose()
			
		#Remove unwannted class 'quote'
		unwanted_classes = post_soup.find_all(class_="quote")
		for uc in unwanted_classes:
			uc.decompose()		
					
		#Remove unwannted tag 'ignore_js_op'
		unwanted_tags = post_soup.find_all("ignore_js_op")
		for ut in unwanted_tags:
			ut.decompose()							
			
		# Pull all the text from the t_fsz div
		messages = post_soup.find_all("td", class_="t_f")		
		
		message_list = []		
		
		for message in messages:
		
			message_list.append(message.get_text())
		
		# Might be better to use "\n".join(message_list) 
		text = " ".join(message_list)
		
		# break into lines and remove leading and trailing space on each
		lines = (line.strip() for line in text.splitlines())
		# break multi-headlines into a line each
		chunks = (phrase.strip() for line in lines for phrase in line.split("  "))
		# drop blank lines
		text = '\n'.join(chunk for chunk in chunks if chunk)

		file = open("pittcssa-bilingual-corpus.txt", "a+")
		file.write(text.encode('utf-8') + "\n")
		file.close()