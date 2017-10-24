import idc
import idaapi
import idautils
from sets import Set
from collections import defaultdict

c = idaapi.decompile(idaapi.get_screen_ea())
func_ea = c.entry_ea

class BasicBlock():
    def __init__(self):
        self.start = 0
        self.end = 0
        self.succs = Set()
        self.preds = Set()
    def isIn(ea):
        return ea>=self.start and ea<=self.end

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
        end = 999999
        for e in sorted(edges):
            if e[0]>bb.start and e[0]<=end:
                bb.end = e[0] 
                bb.succs.add(e[1])
                end = e[0]
        if end == 999999:
            bb.end = 0xFFFF
    for head, bb in BBs.iteritems():
        for h, b in BBs.iteritems():
            if h in bb.succs:
                b.preds.add(head)
    return BBs

BBs = CFG(func_ea)
for h, b in BBs.iteritems():
    print format(h, '04x'), format(b.end, '04x')