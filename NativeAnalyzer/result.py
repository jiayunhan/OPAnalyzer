import datetime
files = ['filter-result.txt','taint-result.txt','libcore-filter-result.txt','libcore-result.txt']
w = open('result.txt', 'w')
ww = open('result-pure.txt', 'w')

filename = ''
t = ''
result = dict()
total_time = 0.0
for f in files:
	r = open(f, 'r')
	line = r.next()
	try:
		while line:
			packagename = line.split(',')[0].strip()
			deltaT = line.split(',')[1].strip()
			if packagename in result:
				result[packagename] = result[packagename] + float(deltaT)
			else:
				result[packagename] = deltaT
			total_time=total_time+float(deltaT)
			line = r.next()
	except:
		pass
	r.close()

for packagename in result.keys():
	w.write(packagename+', '+str(result[packagename])+'\n')
	ww.write(str(result[packagename])+'\n')
w.close()
ww.close()
print total_time/len(result.keys())

