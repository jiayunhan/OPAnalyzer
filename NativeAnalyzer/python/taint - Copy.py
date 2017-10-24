import idc
import idaapi
import idautils
from sets import Set
from collections import defaultdict
from Queue import Queue
#import idascript

def getFunctionWithName(n):
    for f in Functions():
        name = GetFunctionName(f)
        if n in name:
            return f

class BasicBlock():
    def __init__(self):
        self.start = 0
        self.end = 0
        self.succs = Set()
        self.preds = Set()
    def isIn(self, ea):
        return self.end <0xFFFF and ea>=self.start and ea<=self.end

def CFG(function_ea):

    f_start = function_ea
    f_end = FindFuncEnd(function_ea)

    edges = Set()
    boundaries = Set((f_start,))
    
    # For each defined element in the function.
    for head in Heads(f_start, f_end):
    
        # If the element is an instruction
        if isCode(GetFlags(head)):
        
            # Get the references made from the current instruction
            # and keep only the ones local to the function.
            refs = CodeRefsFrom(head, 0)
            refs = Set(filter(lambda x: x>=f_start and x<=f_end, refs))
            
            if refs:
                # If the flow continues also to the next (address-wise)
                # instruction, we add a reference to it.
                # For instance, a conditional jump will not branch
                # if the condition is not met, so we save that
                # reference as well.
                next_head = NextHead(head, f_end)
                if isFlow(GetFlags(next_head)):
                    refs.add(next_head)
                
                # Update the boundaries found so far.
                boundaries.union_update(refs)
                            
                # For each of the references found, and edge is
                # created.
                for r in refs:
                    # If the flow could also come from the address
                    # previous to the destination of the branching
                    # an edge is created.
                    if isFlow(GetFlags(r)):
                        edges.add((PrevHead(r, f_start), r))
                    edges.add((head, r))


    #for e in sorted(edges):
        #print '{:x}, {:x}'.format(*e)

    BBs = defaultdict(BasicBlock)
    for b in sorted(boundaries):
        bb = BasicBlock()
        BBs[b] = bb
        bb.start = b
        #print 'bb: '+format(b, '04x')
        end = 999999
        for e in sorted(edges):
            if e[0]>=bb.start and e[0]<=end:
                flag = True
                for i in range(bb.start+1, e[0]+1):
                    if i in boundaries:
                        flag = False
                        break
                if flag:
                    bb.end = e[0] 
                    bb.succs.add(e[1])
                #print format(e[1], '04x')
                    end = e[0]
        if end == 999999:
            bb.end = 0xFFFF
            #print 'endB', hex(bb.start)
    for head, bb in BBs.iteritems():
        for h, b in BBs.iteritems():
            if h in bb.succs:
                b.preds.add(head)
    return BBs

def taint(Op, ea, taintSet):
    #print Op, format(ea, '04x'), format(BB, '04x')
    def default(ea, taintSet):
        return False
    def two2one(ea, taintSet):
        if idc.GetOpnd(ea, 1) in taintSet:
            taintSet.add(idc.GetOpnd(ea, 0))
            return True
        if idc.GetOpnd(ea, 0) in taintSet and idc.GetOpnd(ea, 1) not in taintSet:
            taintSet.remove(idc.GetOpnd(ea, 0))
        return False
    def one2two(ea, taintSet):
        if idc.GetOpnd(ea, 0) in taintSet:
            taintSet.add(idc.GetOpnd(ea, 1))
            return True
        if idc.GetOpnd(ea, 1) in taintSet and idc.GetOpnd(ea, 0) not in taintSet:
            taintSet.remove(idc.GetOpnd(ea, 1))
        return False
    def function(ea, taintSet):
        Opnd = idc.GetOpnd(ea, 0)
        #print format(ea, '04x'), Opnd
        if Opnd in source and Opnd not in sink:
            print 'source function %s is called, tainting operand %d' %(Opnd, source[Opnd])
            taintSet.add('R'+str(source[Opnd]))
            return True
        elif Opnd in source and 'R'+str(sink[Opnd]) in taintSet:
            sourceOpnd = 'R'+str(source[Opnd])
            while(idc.GetOpnd(ea, 0) != sourceOpnd):
                ea-=2
            taintTarget = idc.GetOpnd(ea, 1)
            taintSet.add(taintTarget)
            print 'Target %s of sink function %s tainted by operand %d' %(taintTarget, Opnd, sink[Opnd]+1)
            return True
        elif Opnd in sink and 'R'+str(sink[Opnd]) in taintSet:
            sinkOpnd = 'R'+str(sink[Opnd])
            while(idc.GetOpnd(ea, 0) != sinkOpnd):
                ea-=2
            taintSource = idc.GetOpnd(ea, 1)
            print 'operand %d of sink function %s tainted by source %s' %(sink[Opnd], Opnd, taintSource)
            return True
        else :
            pass
        return False

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
        return switcher[Op](ea, taintSet)
    else:
        return False


def iterate(BB, taintSet):
    if BB.end < 0xFFFF:
        #print BB.start, BB.end
        for head in Heads(BB.start, BB.end):
            if(isCode(GetFlags(head))):
                #print idc.GetMnem(head), format(head, '04x'), format(BB.start, '04x'), format(BB.end, '04x')
                taint(idc.GetMnem(head), head, taintSet)

def sublistExists(list1, list2):
    return [x for x in xrange(len(list1)) if list1[x:x+len(list2)] == list2]
    #return ''.join(map(str, list2)) in ''.join(map(str, list1))

def findPath(BB, cur_path, target_ea, paths):
    #print BB.start, BB.end
    cur_path.append(BB.start)
    if BB.isIn(target_ea):
        paths.add(tuple(cur_path))
    else:
        for succ in BB.succs:
            idx = sublistExists(cur_path, [BB.start, succ])
            if len(idx) == 0:
                findPath(BBs[succ], cur_path, target_ea, paths)
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
    cur_path.pop()

source = {'accept':0, 'epoll_ctl':0}
sink = {'epoll_ctl':2, 'epoll_wait':0}

#c = idaapi.decompile(idaapi.get_screen_ea())
func_name = 'sub_1614'
func_ea = getFunctionWithName(func_name)
#c = idaapi.decompile(func_ea)
#eamap = c.get_eamap()
#BBs = CFG(c.entry_ea)
BBs = CFG(func_ea)
#func = getFunctionWithName('system')
bbTaintMap = defaultdict(Set)
#entryBB = BBs[c.entry_ea]
#iterate(entryBB)
#acceptBB = BBs[0x1836]
#iterate(acceptBB)
#BFS(c.entry_ea)
paths=set()
'''
findPath(BBs[c.entry_ea], list(), 0x183c, paths)
for path in paths:
    print path
print '#'*20
paths = set()

'''
#findPath(BBs[c.entry_ea], list(), 0x17a4, paths)
#findPath(BBs[func_ea], list(), 0x17a4, paths)
findPath(BBs[0x1836], list(), 0x17a4, paths)
for path in paths:
    print path
    '''
    taintSet = Set()
    for bb in path:
    	iterate(BBs[bb], taintSet) 
    '''
#idc.Exit(0)