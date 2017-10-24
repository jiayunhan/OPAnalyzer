import idaapi
import idc
import idautils
#import networkx
import idascript

def getFunctionWithName(n):
	funcs = []
	for f in Functions():
		name = GetFunctionName(f)
		if n in name:
			funcs.append((f,name))
	funcs.sort(key = lambda x: len(x[1]))
	if funcs:
		print funcs[0][1],
	else:
		print 'NULL',
	#return funcs[0][0]

getFunctionWithName('accept')
getFunctionWithName('system')
idc.Exit(0)
