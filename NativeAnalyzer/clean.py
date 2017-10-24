import os
with open('filelist.txt', 'r') as r:
	for line in r:
		os.system('copy '+line.strip()+' appChinaBoth')
		
		
