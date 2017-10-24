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

class func_call_line():
    def __init__(self, func_name):
        self.map = dict()
        self.name = func_name
    def add(self, ref_ea, line):
        self.map[ref_ea]=line
    def toString(self):
        for k in self.map:
            return self.map[K]

def func_name_to_fcl(func_name):
    for ref_ea in CodeRefsTo(getFunctionWithName(func_name),0):
        if eamap.has_key(ref_ea):
            insnvec = eamap[ref_ea]
            print dir(insnvec)
            lines = []
            for stmt in insnvec:
                qp = idaapi.qstring_printer_t(c.__deref__(), False)
                stmt._print(0, qp)
                ss = qp.s.split('\n')
                for line in ss:
                    print line
                    if func_name in line and 'android_log_print' not in line:
                        lines.append(line)
            #fcl.add(ref_ea, '\n'.join(lines))
        else:
            #print 'reference %X in another function!' %ref_ea
            pass

func_name = 'sub_1614'
func_ea = getFunctionWithName(func_name)
c = idaapi.decompile(func_ea)
eamap = c.get_eamap()
#print dir(eamap)
func_name_to_fcl('accept')
'''
print dir(c)
args = c.arguments
for arg in args:
    print arg.name
    print arg.get_regnum()
    #print dir(arg)
    #print arg.location.get_reginfo()
b = c.body
print dir(b)
print dir(b.details)
eamap = c.get_eamap()
func_name_to_fcl(func_name)

'''
#pc = c.get_pseudocode()
#print dir(c)
#sline = c
#print dir(sline)
#print sline.__class__
#for sline in c:
    #print idaapi.tag_remove(sline.line)
    #print sline.line