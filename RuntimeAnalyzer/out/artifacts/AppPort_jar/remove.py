file = open('anzhi_result_2.txt','r+')
lines = file.readlines()
print len(lines)
out_lines=[]
for line in lines:
	if 'cn.org.fsho' not in line:
		out_lines.append(line)
print len(out_lines)
out_file = open('anzhi_result_filtered.txt','w+')
for line in out_lines:
	out_file.write(line)
out_file.close()
