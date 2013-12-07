import os

tmpF = file("stopwords_en.txt")
out = file("output.txt", "w+")
while True:
	line = tmpF.readline().strip()
	if len(line) == 0:
		break
	out.write("\"" + line + "\", ") 

	
