# -*- coding: utf-8 -*-
import urllib
from bs4 import BeautifulSoup

urlList = []
for i in range(1,98):
	url = 'https://bbs.psucssa.com/index.php?p=/discussions/p' + str(i)
	urlList.append(url)

for item in urlList: 
	print(item)
	html = urllib.urlopen(item).read()
	soup = BeautifulSoup(html, "lxml")

	# Remove bottom links
	last_links = soup.find(class_='PageControls Bottom')
	last_links.decompose()

	# Pull all the text from the Title div
	post_title = soup.find_all(class_='Title')
	print(post_title)
	
	title_a_list = []
	# Pull text from all instances of <a> tag within Title div
	for title in post_title:
		title_a = title.find('a')	
		title_a_list.append(title_a)
	
	for post_link in title_a_list:		
	
		post_url = post_link.get('href')
		print("Scraping url: " + post_url)
			
#		url_item = post_url	
#		post_html = urllib.urlopen(url_item).read()
		post_html = urllib.urlopen(post_url).read()
		post_soup = BeautifulSoup(post_html, "lxml")
		
		last_messages = post_soup.find(class_='P PagerWrap')
		last_messages.decompose()
		
		# Pull all the text from the Message div
		messages = post_soup.find_all(class_='Message')
		
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

		file = open("cssa-bilingual-corpus.txt", "a+")
		file.write(text.encode('utf-8') + "\n")
		file.close()


