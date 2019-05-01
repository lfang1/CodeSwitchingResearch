# -*- coding: utf-8 -*-
import urllib
from bs4 import BeautifulSoup
from urlparse import  urljoin

urlList = []

for i in range(0,4450,25):
	#rent house: 
	url = 'http://cmucssa.net/bbs/viewforum.php?f=3&start=' + str(i)
	urlList.append(url)

for i in range(0,2800,25):
	#second hard market:
	url = 'http://cmucssa.net/bbs/viewforum.php?f=4&start=' + str(i)
	urlList.append(url)
	
for i in range(0,250,25):
	#information sharing:
	url = 'http://cmucssa.net/bbs/viewforum.php?f=9&start=' + str(i)
	urlList.append(url)	
	
	
	

for item in urlList: 
	print(item)
	html = urllib.urlopen(item).read()
	soup = BeautifulSoup(html, "lxml")

	# Remove bottom links
	last_links = soup.find(class_='display-options')
	last_links.decompose()

	# Pull all the text from the topictitle div
	post_title = soup.find_all(class_='topictitle')
	del post_title[0:2]
	print(post_title)
	
	
	for title in post_title:
		url = urljoin(item, title.get('href'))
		print("Scraping url: " + url)
		
		post_html = urllib.urlopen(url).read()
		post_soup = BeautifulSoup(post_html, "lxml")
	
		# Remove bottom links
		last_messages = post_soup.find(id="page-footer")
		last_messages.decompose();
		
		# Pull all the text from the content div
		messages = post_soup.find_all(class_='content')		
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

		file = open("cmucssa-bilingual-corpus.txt", "a+")
		file.write(text.encode('utf-8') + "\n")
		file.close()