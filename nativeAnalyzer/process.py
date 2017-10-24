import datetime
r = open('2-17-filter.txt', 'r')
w = open('filter-result.txt', 'w')

filename = ''
t = ''
line = r.next()
result = dict()
total_time = 0.0

try:
	while line:
		if line.strip().endswith('.so'):
			filename = line.strip()
			packagename = filename.split('#')[0]
			t1 = datetime.datetime.strptime(r.next().strip(), '%H:%M:%S.%f')
			#print t1,
			r.next()
			t2 = datetime.datetime.strptime(r.next().strip(), '%H:%M:%S.%f')
			#print t2,
			deltaT = (t2-t1).total_seconds()
			if deltaT<0:
				deltaT = deltaT + 3600*24
			if packagename in result:
				result[packagename] = result[packagename] + deltaT
			else:
				result[packagename] = deltaT
			total_time=total_time+deltaT
			#print packagename, str(result[packagename])
		line = r.next()
except:
	pass
r.close()
for packagename in result.keys():
	w.write(packagename+', '+str(result[packagename])+'\n')
w.close()
print total_time/len(result.keys())

