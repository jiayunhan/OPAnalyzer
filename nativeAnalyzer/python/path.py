import idc
import idaapi
import idautils
import sys
sys.path.append('C:\\Python27\\Lib\\site-packages')
import networkx
#import idascript

#start = input('Enter the starting function name')
#end = input('Enter the ending function name')

def getFunctionWithName(n):
	funcs = []
	for f in idautils.Functions():
		name = GetFunctionName(f)
		if n in name:
			funcs.append((f,name))
	funcs.sort(key = lambda x: len(x[1]))
	#print funcs[0]
	return funcs[0][0]

        
def findFuncPreds(func_ea):
    pool = set([GetFunctionName(func_ea)])
    flag = set([GetFunctionName(func_ea)])
    result = []
    while pool:
		fname = pool.pop()
		f_ea = getFunctionWithName(fname)
		#print f_ea
		for ref_ea in CodeRefsTo(f_ea,0):
			#print ref_ea.type, XrefTypeName(ref_ea.type)
			#print "04%X" ,ref_ea
			func_name = GetFunctionName(ref_ea)
			#if len(func_name)>0 and func_name not in flag:
			if func_name and func_name not in flag:
				#G.add_edge(func_name, fname)
				#func = get_func(ref_ea)
				result.append((fname, func_name, ref_ea))
				pool.add(func_name)
				flag.add(func_name)
    return result

start = 'accept'
end = 'system'

G = networkx.DiGraph()
PredA = findFuncPreds(getFunctionWithName(start))


PredB = findFuncPreds(getFunctionWithName(end))
for edge in PredA+PredB:
	G.add_edge(edge[1], edge[0])
	G[edge[1]][edge[0]]['ref_ea'] = edge[2]

'''
for edge in PredB:
	G.add_edge(edge[1], edge[0])
	G[edge[1]][edge[0]]['start'] = edge[2]
	G[edge[1]][edge[0]]['end'] = edge[3]
'''

callerAs = [x[1] for x in PredA]
print callerAs
callerBs = [x[1] for x in PredB]
print callerBs
intersect = [x for x in callerAs if x in callerBs]

if len(intersect)>0:
    root = [x for x in callerAs if x in callerBs][0]
    left_branch = networkx.shortest_path(G, source=root, target=start)[::-1]
    right_branch = networkx.shortest_path(G, source=root, target=end)

    left_path = []
    right_path = []
    left_attachp = 0
    right_attachp = 0
    for n in range(len(left_branch)-1):
        callee = left_branch[n]
        caller = left_branch[n+1]
        ref_ea = G[caller][callee]['ref_ea']
        if(caller!=root):
            func = get_func(ref_ea)
            left_path.append((caller, ref_ea, func.endea))
        else:
            left_attachp = ref_ea

    for n in range(len(right_branch)-1):
        caller = right_branch[n]
        callee = right_branch[n+1]
        ref_ea = G[caller][callee]['ref_ea']
        if(caller!=root):
            func = get_func(ref_ea)
            right_path.append((caller, func.startea, ref_ea))
        else:
            right_attachp = ref_ea

    path = left_path+[(root, left_attachp, right_attachp)]+right_path
    print path
#idc.Exit(0)
'''
intersection = [x for x in PredA if x in PredB]
print 'Intersection: ',
for f in intersection:
	print f,
if len(intersection) == 0:
	print 'NULL'
#f = intersection[-1]
#fc = idaapi.FlowChart(idaapi.get_func(getFunctionWithName(f)))
#for bb in fc:
	#print bb.startEA
'''
#idc.Exit(0)