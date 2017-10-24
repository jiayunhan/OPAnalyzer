import idaapi
import idc

c = idaapi.decompile(idaapi.get_screen_ea())
lvars = c.lvars
#for var in lvars:
	#print var.name, format(var.defea, '04x')
eamap = c.get_eamap()

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
		#print 'func_name: '+self.name
		for k in self.map:
			return self.map[k]
	#def findRegs(self):
		#for k in self.map:
			#print idc.GetOpnd(k+4, 0)
			#print idaapi.
			#for ref in XrefsTo(k, 0):
				#print ref.type, XrefTypeName(ref.type)

def var_name_to_reg(var_name):
	print var_name
	for var in lvars:
		if var.name == var_name:
			print dir(var)
			print var.cmt
			print var.is_reg_var()
			print var.is_stk_var()
			defea = var.defea
			print format(defea, '04x')
			if eamap.has_key(defea):
				insnvec = eamap[defea]
				lines = []
				for stmt in insnvec:
					qp = idaapi.qstring_printer_t(c.__deref__(), False)
					stmt._print(0, qp)
					ss = qp.s.split('\n')
					for line in ss:
						if var_name in line:
							lines.append(line)
		else:
			pass
	#for line in lines:
		#print line
	
def func_name_to_fcl(func_name):
	fcl = func_call_line(func_name)
	for ref_ea in CodeRefsTo(getFunctionWithName(func_name),0):
		if eamap.has_key(ref_ea):
			insnvec = eamap[ref_ea]
			lines = []
			for stmt in insnvec:
				qp = idaapi.qstring_printer_t(c.__deref__(), False)
				stmt._print(0, qp)
				ss = qp.s.split('\n')
				for line in ss:
					if func_name in line and 'android_log_print' not in line:
						lines.append(line)
			fcl.add(ref_ea, '\n'.join(lines))
		else:
			#print 'reference %X in another function!' %ref_ea
			pass
	return fcl

func_name_accept = 'accept'
func_name_system = 'system'
func_name_wait = 'epoll_wait'
func_name_ctl = 'epoll_ctl'

fcl_1 = func_name_to_fcl(func_name_accept)
#fcl_1.toString()
var_name = fcl_1.toString().split('=')[0].strip()
var_name_to_reg(var_name)
fcl_2 = func_name_to_fcl(func_name_system)
#fcl_2.toString()
fcl_3 = func_name_to_fcl(func_name_wait)
var_name = fcl_3.toString().split('=')[0].strip()
var_name_to_reg(var_name)
#fcl_3.toString()
fcl_4 = func_name_to_fcl(func_name_ctl)
#fcl_4.toString()
#print idc.GetOpnd(0x177E, 1)
#print idc.GetMnem(0x177E)