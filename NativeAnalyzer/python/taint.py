import idc
import idaapi
import idautils
from sets import Set
from collections import defaultdict
import sys
sys.path.append('C:\\Python27\\Lib\\site-packages')
import networkx
from Queue import Queue
import idascript

def getFunctionWithName(n):
    funcs = []
    for f in idautils.Functions():
        name = GetFunctionName(f)
        if n in name:
            funcs.append((f,name))
    funcs.sort(key = lambda x: len(x[1]))
    #print funcs[0]
    return funcs[0][0]

def isIn(BB, ref_ea):
    if BB.startEA<=ref_ea and BB.endEA>ref_ea:
        return 0
    if BB.startEA<=ref_ea and BB.startEA==BB.endEA:
        return 0
    if BB.startEA>ref_ea:
        return -1
    if BB.endEA<=ref_ea:
        return 1


def CFG(func_ea):
    BBs = []
    f = get_func(func_ea)
    fc = FlowChart(f)
    for block in fc:
        BBs.append(block)
    return sorted(BBs, key=lambda b:b.startEA)


def taint(Op, ea, taintSet):
    #print Op, format(ea, '04x'), format(BB, '04x')
    def default(ea, taintSet):
        pass
        #return False
    def two2one(ea, taintSet):
        if idc.GetOpnd(ea, 1) in taintSet:
            taintSet.add(idc.GetOpnd(ea, 0))
            #return True
        if idc.GetOpnd(ea, 0) in taintSet and idc.GetOpnd(ea, 1) not in taintSet:
            taintSet.remove(idc.GetOpnd(ea, 0))
        #return False
    def one2two(ea, taintSet):
        if idc.GetOpnd(ea, 0) in taintSet:
            taintSet.add(idc.GetOpnd(ea, 1))
            #return True
        if idc.GetOpnd(ea, 1) in taintSet and idc.GetOpnd(ea, 0) not in taintSet:
            taintSet.remove(idc.GetOpnd(ea, 1))
        #return False
    def function(ea, taintSet):
        Opnd = idc.GetOpnd(ea, 0)
        #print format(ea, '04x'), Opnd, taintSet
        if Opnd in source and Opnd not in sink:
            #print 'source function %s is called, tainting operand %d' %(Opnd, source[Opnd]),
            taintSet.add('R'+str(source[Opnd]))
            #print taintSet
            #return True
        elif Opnd in source and 'R'+str(sink[Opnd]) in taintSet:
            sourceOpnd = 'R'+str(source[Opnd])
            while(idc.GetOpnd(ea, 0) != sourceOpnd):
                ea-=2
            taintTarget = idc.GetOpnd(ea, 1)
            taintSet.add(taintTarget)
            #print 'Target %s of sink function %s tainted by operand %d' %(taintTarget, Opnd, sink[Opnd]+1), taintSet
            #return True
        elif Opnd in sink and 'R'+str(sink[Opnd]) in taintSet:
            sinkOpnd = 'R'+str(sink[Opnd])
            while(idc.GetOpnd(ea, 0) != sinkOpnd):
                ea-=2
            taintSource = idc.GetOpnd(ea, 1)
            #print 'operand %d of sink function %s tainted by source %s' %(sink[Opnd], Opnd, taintSource)
            #return True
        else :
            #pass
            func_ea = getFunctionWithName(Opnd)
            f = get_func(func_ea)
            taintFunction(Opnd, f.startEA, 0, taintSet)

        #return False

    switcher = {
        'PUSH': default,
        'LDR': two2one,
        'MOV': two2one,
        'ADD': two2one,
        'STR': one2two,
        'LSL': two2one,
        'LSR': two2one,
        'ORR': two2one,
        'BLX': function,
        'CMP': default,
        'SUB': two2one,
        'POP': default,
        'B': default,
        'BX': function,
        'BL': default,
        'NEG': two2one,
        'TST': default,
        'TEQ': default,
        'BIC': two2one,
        'STM': default,
        'RSB': one2two,
        'AND': two2one,
        'LDM': default,
        'CMN': default,
        'ADC': two2one,
        'FLDM': default,
        'FSTM': default,
        'LDC': default,
        'LDC2': default,
        'STC': default,
        'STC2': default,
        'MVN': two2one
    }
    if Op in switcher:
        switcher[Op](ea, taintSet)
    #else:
        #print Op
        #pass
        #return False


def iterate(BB, taintSet):
    for head in Heads(BB.startEA, BB.endEA):
        if(isCode(GetFlags(head))):
                #print idc.GetMnem(head), format(head, '04x'), format(BB.start, '04x'), format(BB.end, '04x')
            taint(idc.GetMnem(head), head, taintSet)

def sublistExists(list1, list2):
    return [x for x in xrange(len(list1)) if list1[x:x+len(list2)] == list2]
    #return ''.join(map(str, list2)) in ''.join(map(str, list1))

def findPath(BB, target_ea, cur_path, paths):
    #print BB.start, BB.end
    cur_path.append(BB.startEA)
    #print hex(BB.startEA),
    #print isIn(BB, target_ea)
    if isIn(BB, target_ea)==0:
        #print 'Found: %04x' %BB.startEA
        paths.add(tuple(cur_path))
    else:
        succNum = 0
        for succ in BB.succs():
            succNum+=1
            idx = sublistExists(cur_path, [BB.startEA, succ.startEA])
            if len(idx) == 0:
                findPath(succ, target_ea, cur_path, paths)
            else:
                pass
                '''
                len(idx)==1 and idx[0]>0:
                loc = idx[0]
                idx_ = sublistExists(cur_path, [cur_path[loc-1], BB.start, succ])
                if len(idx_)==1:
                    findPath(BBs[succ], cur_path, target_ea, paths)
            else:
                pass
                '''
        if succNum==0:
            if BB.endEA == target_ea:
                paths.add(tuple(cur_path))
    cur_path.pop()

def biSearch(bbList, ref_ea, start, end):
    #print map(lambda b:hex(b.startEA), bbList), hex(ref_ea), start, end,
    mid = (start+end)/2
    #print mid
    if isIn(bbList[mid], ref_ea) == 1:
        return biSearch(bbList, ref_ea, mid+1, end)
    if isIn(bbList[mid], ref_ea) == 0:
        return bbList[mid]
    if isIn(bbList[mid], ref_ea) == -1:
        return biSearch(bbList, ref_ea, start, mid)

def findEnd(bbList):
    result = []
    for bb in bbList:
        succNum = 0
        for succ in bb.succs():
            succNum+=1
        if succNum==0:
            result.append(bb.startEA)
    #print 'end: ', map(lambda n:hex(n), result)
    return result



#c = idaapi.decompile(idaapi.get_screen_ea())
def taintFunction(func_name, start, end, taintSet):
    func_ea = getFunctionWithName(func_name)
#c = idaapi.decompile(func_ea)
#eamap = c.get_eamap()
#BBs = CFG(c.entry_ea)
    BBs = CFG(func_ea)
    startBB = biSearch(BBs, start, 0, len(BBs))
    endList = []
    if end==0:
        endList = findEnd(BBs)
    else:
        endList.append(end)

#func = getFunctionWithName('system')
#bbTaintMap = defaultdict(Set)
#entryBB = BBs[c.entry_ea]
#iterate(entryBB)
#acceptBB = BBs[0x1836]
#iterate(acceptBB)
#BFS(c.entry_ea)
    taintSetCpy = taintSet.copy()
    for end in endList:
        paths=Set()
        findPath(startBB, end, list(), paths)
        for path in paths:
            tmp = taintSetCpy.copy()
            #print 'path: ', map(lambda n:hex(n), [n for n in path])
            for bb in path:
            	iterate(biSearch(BBs, bb, 0, len(BBs)), tmp)
            #print tmp
            taintSet.update(tmp)
        #print end, map(lambda n:hex(n), list(path)), taintSet

#idc.Exit(0)

def main(func_name, start, end, taintSet):
    taintFunction(func_name, start, end, taintSet)
    return taintSet

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
            if len(func_name)>0 and func_name not in flag:
                #G.add_edge(func_name, fname)
                #func = get_func(ref_ea)
                result.append((fname, func_name, ref_ea))
                pool.add(func_name)
                flag.add(func_name)
            elif len(func_name)==0:
                print 'Thumb'
                idc.Exit(0)
    return result

start = 'accept'
end = 'system'
source = {'accept':0, 'epoll_ctl':0}
sink = {'epoll_ctl':2, 'epoll_wait':0}

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
callerBs = [x[1] for x in PredB]
intersect = [x for x in callerAs if x in callerBs]

if len(intersect)>0:
	root = [x for x in callerAs if x in callerBs][0]
	left_branch = networkx.shortest_path(G, source=root, target=start)[::-1]
	right_branch = networkx.shortest_path(G, source=root, target=end)

	left_path = []
	right_path = []
	left_attachP = 0
	right_attachP = 0
	for n in range(len(left_branch)-1):
		callee = left_branch[n]
		caller = left_branch[n+1]
		ref_ea = G[caller][callee]['ref_ea']
		if(caller!=root):
			func = get_func(ref_ea)
			left_path.append((caller, ref_ea, func.endEA))
		else:
			left_attachP = ref_ea

	for n in range(len(right_branch)-1):
		caller = right_branch[n]
		callee = right_branch[n+1]
		ref_ea = G[caller][callee]['ref_ea']
		if(caller!=root):
			func = get_func(ref_ea)
			right_path.append((caller, func.startEA, ref_ea))
		else:
			right_attachP = ref_ea

	path = left_path+[(root, left_attachP, right_attachP)]+right_path
	taintSet = Set()
	for p in path:
		taintSet = main(p[0], p[1], p[2], taintSet)
	print taintSet
else:
	print 'NULL'
idc.Exit(0)
