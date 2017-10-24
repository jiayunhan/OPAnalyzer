from idaapi import *

def getFunctionWithName(n):
    for f in Functions():
		name = GetFunctionName(f)
		if n in name:
			return f

tif = tinfo_t()
get_tinfo2(getFunctionWithName('accept'), tif)
funcdata = func_type_data_t()
tif.get_func_details(funcdata)
#funcdata.size()
for i in xrange(funcdata.size()):
    print "Arg %d: %s (of type %s, and of location: %s)" % (i, funcdata[i].name, print_tinfo('', 0, 0, PRTYPE_1LINE, funcdata[i].type, '', ''), funcdata[i].argloc.atype())