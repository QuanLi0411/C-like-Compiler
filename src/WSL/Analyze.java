/*******************************************************
	�ļ�	analyze.java
	˵��	�����������������ʵ��
	����	������ű����Ϣ��,����ͳһ�ķ��ű�ṹ,���λkind���ֲ�ͬ����
		�������������
*******************************************************/

package WSL;
import java.util.ArrayList;
import BXC.Global.DecKind;
import BXC.Global.NodeKind;
import BXC.Global.StmtKind;
import BXC.Global.VarKind;
import BXC.Global.treeNode;
import BXC.Global;
import BXC.Parse;
import BXC.ParseLL1;

public class Analyze {

	public static void main(String[] args) {
		Analyze myAnalyze = new Analyze();
		myAnalyze.run();
	}

	public static Global global = new Global();

	public static Parse myParse = new Parse();
	public static ParseLL1 myParseLL1 = new ParseLL1();

	public Analyze() {
		/* scopeջ�Ĳ��� */
		Level = -1;
	}

	/**********************************************
	  			���������Ҫ�õ������ͼ���������
	**********************************************/

	/* ��ʶ�������� */
	public enum IdKind {
		typeKind, varKind, funcKind
	};

	/* ʹ��scopeջ�ľֲ����ű��������õ���scopeջ */
	public ArrayList<ArrayList<SymbTable>> scope = new ArrayList<ArrayList<SymbTable>>();
	
	/* scopeջ�Ĳ���0��1 */
	public int Level;

	/* ��ͬ��ı���ƫ�� */
	public int Off;

	/* ��¼��ǰ���displayOff */
	public int savedOff;
	
	/* �������׷�ٱ�־,����ñ�־ΪTRUE,�����ű����Ͳ���д���б��ļ�listing */
	public boolean TraceTable = true;

	public boolean TraceCode = true;

	/**********************************************
						�����ڲ���ʾ
	**********************************************/

	/* ���͵�ö�ٶ��� */
	public enum TypeKind {
		intTy, charTy, boolTy, arrayTy, structTy
	};

	/* �������ͽṹ���� */
	public class ArrayAttr {
		public TypeIR indexTy; /* ָ�������±����͵��ڲ���ʾ */
		public TypeIR elemTy; /* ָ������Ԫ�����͵��ڲ���ʾ */
		public int size; /* ��¼�������͵Ĵ�С */
	}

	/* �ṹ�����͵�Ԫ�ṹ���� */
	public class FieldChain {
		public String id; /* ������ */
		public int off; /* ���ڼ�¼�е�ƫ�� */
		public TypeIR unitType; /* ���г�Ա������ */
	}

	/* ���͵��ڲ��ṹ���� */
	public class TypeIR {
		
		public class Attr {
			public ArrayAttr arrayAttr = new ArrayAttr();
			public ArrayList<FieldChain> structBody = new ArrayList<FieldChain>();/*��¼�����е�����*/
		}

		public int size; /* ������ռ�ռ��С */
		public TypeKind kind;
		public Attr more = new Attr(); /* ��չ����,��ͬ�����в�ͬ������ */
	}

	/* �βα�Ľṹ���� */
	public class ParamTable {
		public SymbTable table; /* ָ����β����ڷ��ű��еĵ�ַ��� */
	}

	/* ��ʶ�������Խṹ���� */
	public class AttributeIR {
		public class Attr {
			public class VarAttr {
				public int level; /* �ñ����Ĳ��� */
				public int off; /* �ñ�����ƫ�� */
			}

			public class FuncAttr {
				public int level; /* �ù��̵Ĳ��� */
				public ArrayList<ParamTable> param = new ArrayList<ParamTable>(); /* ������ */
				public int mOff; /* ���̻��¼�Ĵ�С */
				public int nOff; /* sp��display���ƫ���� */
			}

			public VarAttr varAttr = new VarAttr(); /* ������ʶ�������� */
			public FuncAttr funcAttr = new FuncAttr(); /* ��������ʶ�������� */
		}

		public TypeIR idtype; /* ָ���ʶ���������ڲ���ʾ */
		public IdKind kind; /* ��ʶ�������� */
		public Attr more = new Attr(); /* ��չ����,��ʶ���Ĳ�ͬ�����в�ͬ������ */
	}
	
	/* ���ű�Ľṹ���� */
	public class SymbTable {
		public String idName = "";
		public AttributeIR attrIR = new AttributeIR();
	}

	/*********************************************************
	 						���ű���ز���
	*********************************************************/

	/********************************************************/
	/* 	���� 	PrintFieldTable 								*/
	/* 	����	��ӡ��¼���͵���� 									*/
	/* 	˵�� 													*/
	/********************************************************/
	public void PrintFieldChain(SymbTable table) {
		ArrayList<FieldChain> fieldchain = table.attrIR.idtype.more.structBody;
		global.pw.println();
		global.pw.printf("----------------------- ���:%s -----------------------",table.idName);
		global.pw.println();
		global.pw.println();
		for (int i=0; i < fieldchain.size(); i++) {
			/* �����ʶ������ */
			global.pw.printf("%10s:   ", fieldchain.get(i).id);
			/* �����ʶ����������Ϣ */
			switch (fieldchain.get(i).unitType.kind) {
			case intTy:
				global.pw.print("intTy     ");
				break;
			case charTy:
				global.pw.print("charTy    ");
				break;
			case arrayTy:
				global.pw.print("arrayTy   ");
				break;
			case structTy:
				global.pw.print("structTy  ");
				break;
			default:
				global.pw.print("error type");
				break;
			}
			global.pw.printf("Off = %d", fieldchain.get(i).off);
			global.pw.println();
		}
		global.pw.flush();
	}

	/********************************************************/
	/* 	���� 	PrintOneLayer									*/
	/* 	���� 	��ӡ���ű��һ�� 										*/
	/* 	˵�� 	�з��ű��ӡ����PrintSymbTable����						*/
	/********************************************************/
	public void PrintOneLayer(int level) {
		ArrayList<SymbTable> t = scope.get(level);
		global.pw.println();
		global.pw.printf("---------------------- ��%d��ķ��ű� ----------------------",level);
		global.pw.println();
		global.pw.println();

		for (int i=0; i<t.size(); i++) {
			/* �����ʶ������ */
			global.pw.printf("%10s:   ",t.get(i).idName);
			AttributeIR Attrib = t.get(i).attrIR;
			/* �����ʶ����������Ϣ�����̱�ʶ������ */
			if (Attrib.idtype != null) /* ���̱�ʶ�� */
				switch (Attrib.idtype.kind) {
				case intTy:		global.pw.print("intTy     ");	break;
				case charTy: 	global.pw.print("charTy    "); break;
				case arrayTy: 	global.pw.print("arrayTy   "); break;
				case structTy:	global.pw.print("structTy  ");	break;
				default:		global.pw.print("error type!  ");
				}
			/* �����ʶ������𣬲����ݲ�ͬ���������ͬ�������� */
			switch (Attrib.kind) {
			case typeKind:
				global.pw.print("typekind  ");
				break;
			case varKind:
				global.pw.print("varkind   ");
				global.pw.printf("Level = %d  ", Attrib.more.varAttr.level);
				global.pw.printf("Offset = %d  ", Attrib.more.varAttr.off);
				break;
			case funcKind:
				global.pw.print("funckind  ");
				global.pw.printf("Level = %d  ", Attrib.more.funcAttr.level);
				global.pw.printf("nOff = %d  ", Attrib.more.funcAttr.nOff);
				break;
			default:
				global.pw.print("error  ");
			}
			global.pw.println();
		}
		global.pw.flush();
	}

	/********************************************************/
	/* 	����	PrintSymbTable									*/
	/* 	���� 	��ӡ���ɵķ��ű�										*/
	/* 	˵��													*/
	/********************************************************/
	public void PrintSymbTable() {
		/* ������0��ʼ */
		for(int level=0;level<scope.size()&&level<2;level++)
			PrintOneLayer(level);
	}

	/********************************************************/
	/* 	����	EnterNextLevel 									*/
	/* 	����	�����շ��ű� 										*/
	/* 	˵��	������һ���µľֲ�����λʱ�����ñ��ӳ��� 						*/
	/* 	����	����һ���շ��ű�table��������1��ƫ�Ƴ�ʼ��Ϊ0 				*/
	/********************************************************/
	public void EnterNextLevel() {
		Level ++;	/* ������һ */
		Off = 0;	/* ƫ�Ƴ�ʼ�� */
		scope.add(new ArrayList<SymbTable>());
	}

	/********************************************************/
	/* 	����	ReturnLastLevel 								*/
	/* 	���� 	������ǰ���ű� 										*/
	/* 	˵��	�˳�һ���ֲ�����ʱ�����ñ��ӳ��� 							*/
	/* 	����	������1����������ǰ���ű� 								*/
	/********************************************************/
	public void ReturnLastLevel() {
		Level --;
	}

	/********************************************************/
	/* 	���� 	enterTable 										*/
	/* 	���� 	�ǼǱ�ʶ��������										*/
	/* 	˵�� 	1.�ѱ�ʶ��id������attrIR�Ǽǵ����ű��У������صǼ���ĵ�ַ��		*/
	/* 		2.����ڱ������Ƿ����ظ������������id���Ѵ����򷵻�true��		*/
	/* 		        �޴���false�� 									*/
	/********************************************************/
	public SymbTable enterTable(int lineno, String id, AttributeIR attrIR) {

		boolean isRedeclare = false;	/* false��ʾ���ظ��������� */
		SymbTable table = new SymbTable();
		
		/* ����ظ�������� */
		if (scope.get(Level).isEmpty()) {
			//table.attrIR.kind = IdKind.typeKind;
			scope.get(Level).add(table);
		} else {
			/* �ڸò���ű��ڼ���Ƿ����ظ�������� */
			for (int i=0; i<scope.get(Level).size(); i++)
				if (id.equals(scope.get(Level).get(i).idName)) {
					Global.Error = true;
					isRedeclare = true;
				}
			if (isRedeclare)
				ErrorPrompt(lineno, id, "is declared repeatedly!");
			else {
				//table.attrIR.kind = IdKind.typeKind;
				scope.get(Level).add(table);
			}
		}
		
		/* ����ʶ���������ԵǼǵ����� */
		table.idName = id;
		table.attrIR = attrIR;

		return table;
	}

	/********************************************************/
	/* 	����	FindEntry 										*/
	/* 	����	Ѱ�ұ����ַ										*/
	/* 	˵��	�Ը����ı�ʶ��id (idΪ�ַ�������) ���������ַ, 			*/
	/* 		����entry��ʵ�ε�Ԫ�з��ر����ַ��������ű���û 				*/
	/* 		�����ҵ�id��,�򷵻�presentΪ0,�����еĲ���entry 			*/
	/* 		��ֵΪָ��ñ����ַ��ָ��;����,present��ֵΪ1�� 				*/
	/********************************************************/
	public SymbTable findTable(int lineno, String id) {
		
		boolean isFind = false;
		int lev = Level, index = 0;

		/* ����ڱ�����û�в鵽����ת����һ���ֲ��������м������� */
		for (lev=Level; lev>=0&&!isFind; lev--)
			for (index=0; index<scope.get(lev).size()&&!isFind; index++)
				if (id.equals(scope.get(lev).get(index).idName))
					isFind = true; /* ����ҵ���ͬ���ֵı�ʶ�����򷵻�TRUE */
		
		if (!isFind)
			ErrorPrompt(lineno, id, "is not be declared!");

		return scope.get(lev+1).get(index-1);
	}

	/***********************************************************/
	/* ������ BaseType */
	/* �� �� ������ǰ�������ڲ���ʾ */
	/* ˵ �� ����Ϊ���ͣ��������ظ����͵��ڲ���ʾ�ĵ�ַ */
	/***********************************************************/
	public TypeIR BaseTypeToPtr(TypeKind kind) {
		/* �ڴ��ж�̬������䵥Ԫ�� ����ָ��õ�Ԫ�������ڲ���ʾ����ָ��t */
		TypeIR ptr = new TypeIR();
		switch (kind) {
		case intTy:
		case charTy:
		case boolTy:
			ptr.kind = kind;
			ptr.size = 1;
			break;
		case arrayTy:
			ptr.kind = kind;
			break;
		case structTy:
			ptr.kind = kind;
			break;
		}
		return ptr;
	}

	/***********************************************************/
	/* ������ ErrorPrompt */
	/* �� �� ������ʾ */
	/* ˵ �� ������ļ�����ʾ������ʾ������ȫ����Error��ֵΪ1 */
	/***********************************************************/
	public void ErrorPrompt(int line, String name, String message) {
		global.pw.print(">>> error :   ");
		global.pw.printf("Analyze error at line %d, %s %s",line,name,message);
		global.pw.println();
		global.pw.flush();
		Global.Error = true;
		System.exit(0);
	}
	
	/*************************************************************
	  						�����������ʵ�� 
	*************************************************************/
	
	/************************ ���͵�������� **************************/
	
	/************************************************************/
	/* 	���� 	DecToPtr 											*/
	/* 	���� 	�ú�������������ͷ����Ĺ��� 									*/
	/* 	˵�� 	�����﷨���ĵ�ǰ������͡��������ǰ���͵��ڲ��� 					*/
	/* 		ʾ���������ַ���ظ�Ptr�����ڲ���ʾ�ĵ�ַ. 						*/
	/************************************************************/
	public TypeIR DecToPtr(treeNode t, DecKind deckind) {
		TypeIR Ptr = null;
		switch (deckind) {
		case IntK:
			Ptr = BaseTypeToPtr(TypeKind.intTy);
			break; /* ����Ϊ�������� */
		case CharK:
			Ptr = BaseTypeToPtr(TypeKind.charTy);
			break; /* ����Ϊ�ַ����� */
		case ArrayK:
			Ptr = arrayType(t);
			break; /* ����Ϊ�������� */
		case StructK:
			Ptr = structType(t);
			break; /* ����Ϊ�ṹ������ */
		case IdK:
			Ptr = nameType(t);
			break; /* ����Ϊ�Զ����ʶ�� */
		}
		return Ptr;
	}

    /************************************************************/
    /* 	����  	arrayType                                        	*/
    /* 	����  	�ú��������������͵��ڲ���ʾ                     							*/
    /* 	˵��  	����Ϊ��������ʱ����Ҫ����±��Ƿ�Ϸ���         						*/
    /************************************************************/
	public TypeIR arrayType(treeNode t) {
		
		TypeIR Ptr0 = null;
		TypeIR Ptr1 = null;
		TypeIR Ptr = null;

		/* �������ͷ��������������±����� */
		Ptr0 = DecToPtr(t, DecKind.IntK);
		/* �������ͷ�������������Ԫ������ */
		Ptr1 = DecToPtr(t, t.attr.arrayAttr.childType);
		/* ָ��һ�´�����������Ϣ�� */
		Ptr = BaseTypeToPtr(TypeKind.arrayTy);
		/* ���㱾���ͳ��� */
		Ptr.size =(t.attr.arrayAttr.size)*(Ptr1.size);
		/* ��д������Ϣ */
		Ptr.more.arrayAttr.indexTy = Ptr0;
		Ptr.more.arrayAttr.elemTy = Ptr1;
		Ptr.more.arrayAttr.size = t.attr.arrayAttr.size;
		
		return Ptr;
	}

	/************************************************************/
	/* 	����	structType */
	/* 	���� 	�ú��������¼���͵��ڲ���ʾ */
	/* 	˵�� 	����Ϊ��¼����ʱ�����ɼ�¼����ɵġ����ڲ��ڵ��� */
	/* 		Ҫ����3����Ϣ:һ�ǿռ��Сsize���������������־ */
	/* 		structTy;�����岿�ֵĽڵ��ַbody����¼�����е� */
	/* 		�������Ǳ�ʶ���Ķ����Գ��֣������Ҫ��¼�����ԡ� */
	/************************************************************/
	public TypeIR structType(treeNode t) {

		ArrayList<FieldChain> body = new ArrayList<FieldChain>();
		int off = 0,size = 0;
		t = t.child[0]; /* ���﷨���Ķ��ӽڵ��ȡ����Ϣ */
		while (t != null) /* ѭ������ */
		{
			/* ��дptr2ָ������ݽڵ�,�˴�ѭ���Ǵ���������int a,b; */
			for (int i = 0; i < t.idnum; i++) {
				/* �����µ������͵�Ԫ�ṹPtr2 */
				FieldChain Fc = new FieldChain();

				/* ��дPtr2�ĸ�����Ա���� */
				Fc.id = t.name[i];
				Fc.off = off + size;
				Fc.unitType = DecToPtr(t, t.kind.dec);

				off = Fc.off;
				size = Fc.unitType.size;
				body.add(Fc);
			}
			/* ������ͬ���͵ı�����ȡ�﷨�����ֵܽڵ� */
			t = t.sibling;
		}

		/* �����¼�����ڲ��ṹ */
		
		/* �½��ṹ���͵Ľڵ� */
		TypeIR Ptr = BaseTypeToPtr(TypeKind.structTy);
		/* ȡPtr2��offΪ���������¼��size */
		Ptr.size = off + size;
		/* �����������¼���͵�body���� */
		Ptr.more.structBody = body;

		return Ptr;
	}

	/************************************************************/
	/* 	����	nameType */
	/* 	����	�ú��������ڷ��ű���Ѱ���Ѷ������������ */
	/* 	˵��	����Ѱ�ұ����ַ����FindEntry�������ҵ��ı����ַ */
	/* 		ָ��entry�����presentΪFALSE���������������� */
	/* 		������ű��еĸñ�ʶ����������Ϣ�������ͣ������ */
	/* 		�ͱ�ʶ�����ú�������ָ��ָ����ű��еĸñ�ʶ���� */
	/* 		�����ڲ���ʾ�� */
	/************************************************************/
	public TypeIR nameType(treeNode t) {

		TypeIR Ptr = null;
		
		/* ���ͱ�ʶ��Ҳ��Ҫ��ǰ����� */
		SymbTable table = findTable(t.lineno, t.attr.type_name);
		
		/* ���ñ�ʶ���Ƿ�Ϊ���ͱ�ʶ�� */
		if (table.attrIR.kind == IdKind.typeKind)
			Ptr = table.attrIR.idtype;
		else
			ErrorPrompt(t.lineno, t.attr.type_name, "used before typed!");
		
		return Ptr;
	}

	/************************ ������������� **************************/

	/************************************************************/
	/* 	���� 	TypeDecPart */
	/* 	���� 	�ú������������������������ */
	/* 	˵�� 	��������Tʱ���������ڲ��ڵ�TPtr������"typedef idname T"�� */
	/* 		����ű����鱾�������������Ƿ����ظ��������. */
	/************************************************************/
	public void TypeDecPart(treeNode t) {

		AttributeIR attrIr = new AttributeIR();
		attrIr.kind = IdKind.typeKind;	/* �����ķ��ű����� */

		/* ���ü�¼���Ժ����������Ƿ��ظ����������ڵ�ַ */
		SymbTable table = enterTable(t.lineno, t.name[0], attrIr);

		table.attrIR.idtype = DecToPtr(t, t.kind.dec);
	}

	/************************************************************/
	/* 	����	VarDecPart */
	/* 	����	�ú����������������������� */
	/* 	˵�� 	������������ʶ��idʱ����id�Ǽǵ����ű��У������ */
	/* 		���Զ��壻��������ʱ���������ڲ���ʾ�� */
	/************************************************************/
	public void VarDecPart(treeNode t) {

		for (int i = 0; i < t.idnum; i++) {
			AttributeIR attrIr = new AttributeIR();
			attrIr.kind = IdKind.varKind;	/* �����ķ��ű����� */
			
			attrIr.idtype = DecToPtr(t, t.kind.dec);
			attrIr.more.varAttr.level = Level;
			/* ����ֵ�ε�ƫ�� */
			attrIr.more.varAttr.off = Off;
			Off = Off + attrIr.idtype.size;

			/* �ǼǸñ��������Լ�����,�������������ڲ�ָ�� */
			t.table[i] = enterTable(t.lineno, t.name[i], attrIr);
		}
		/* ��¼��ʱƫ�ƣ�����������д������Ϣ���nOff��Ϣ */
		savedOff = Off;
	}

	/************************************************************/
	/* ������FuncDecPart */
	/* �� �� �ú����������������������� */
	/* ˵ �� �ڵ�ǰ����ű�����д���̱�ʶ�������ԣ����²���� */
	/* ������д�βα�ʶ�������ԡ� */
	/************************************************************/
	public void FuncDecPart(treeNode t) {
		
		AttributeIR attrIr = new AttributeIR();
		attrIr.kind = IdKind.funcKind;	/* �����ķ��ű����� */
		attrIr.idtype = DecToPtr(t, t.kind.dec);
		attrIr.more.funcAttr.level = 1;
		
		/* �ǼǺ����ķ��ű��� */
		SymbTable table = enterTable(t.lineno, t.name[0], attrIr);
		/* �����β������� */
		t.table[0] = table;

		/* t.child[0]�Ǻ����Ĳ������� */
		table.attrIR.more.funcAttr.param = ParaDecList(t.child[0]);
		table.attrIR.more.funcAttr.nOff = savedOff;
		savedOff = 0;	/* �ù����� */
		/* ���̻��¼�ĳ��ȵ���nOff����display��ĳ���,diplay��ĳ��ȵ��ڹ������ڲ���(Ҳ����1)��1 */
		table.attrIR.more.funcAttr.mOff = table.attrIR.more.funcAttr.nOff + 2;

		/* t.child[1].child[0]�Ǻ���������岿�� */
		Body(t.child[1].child[0]);

		/* �������ֽ�����ɾ�������β�ʱ���½����ķ��ű� */
		if (Level != -1)
			ReturnLastLevel();/* ������ǰscope */
	}
	
	/************************************************************/
	/* ������ ParaDecList */
	/* �� �� �ú���������ͷ�еĲ���������������� */
	/* ˵ �� ���µķ��ű��еǼ������βεı�������βα���� */
	/* ��ַ������paraָ���䡣 */
	/************************************************************/
	public ArrayList<ParamTable> ParaDecList(treeNode t) {

		ArrayList<ParamTable> list = new ArrayList<ParamTable>();
		EnterNextLevel(); /* �����µľֲ����� */
		Off = 10; /* �ӳ����еı�����ʼƫ����Ϊ10 */
		
		if (t != null) {
			/*������������*/
			while(t!=null) {
				VarDecPart(t);
				t = t.sibling;
			}
			
			for (int i=0;i<scope.get(Level).size();i++) {
				/* �����βη��ű���ʹ�����������ű��param�� */
				ParamTable Pt = new ParamTable();
				Pt.table = scope.get(Level).get(i);
				list.add(Pt);
			}
		}
		return list; /* �����βη��ű��ͷָ�� */
	}

	/******************* ִ���岿�ֵ�������� *********************/

	/************************************************************/
	/* ������ Body */
	/* �� �� �ú�������ִ���岿�ֵ�������� */
	/* ˵ �� TINY����ϵͳ��ִ���岿�ּ�Ϊ������У���ֻ�账�� */
	/* ������в��֡� */
	/************************************************************/
	public void Body(treeNode t) {
		while (t != null) {
			switch (t.nodeKind) {
			case StmLK:
				treeNode p = t.child[0];
				while (p != null) {
					Statement(p); /* �������״̬������ */
					p = p.sibling;
				}
				break;
			case StmtK:
				Statement(t); /* �������״̬������ */
				break;
			case TypeK:
				TypeDecPart(t);
				break;
			case DecK:
				VarDecPart(t);
				break;
			default:
				ErrorPrompt(t.lineno, "", "no this node kind in body list!");
				break;
			}
			t = t.sibling; /* ���ζ����﷨��������е��ֵܽڵ� */
		}
	}

	/************************************************************/
	/* ������ statement */
	/* �� �� �ú����������״̬ */
	/* ˵ �� �����﷨���ڵ��е�kind���ж�Ӧ��ת�����ĸ���� */
	/* ���ͺ����� */
	/************************************************************/
	public void Statement(treeNode t) {
		switch (t.kind.stmt) {
		case AssignK:
			AssignStmt(t);
			break;
		case CallK:
			CallStmt(t);
			break;
		case IfK:
			IfStmt(t);
			break;
		case WhileK:
			WhileStmt(t);
			break;
		case ForK:
			ForStmt(t);
			break;
		case CinK:
			CinStmt(t);
			break;
		case CoutK:
			CoutStmt(t);
			break;
		case ReturnK:
			ReturnStmt(t);
			break;
		default:
			ErrorPrompt(t.lineno, "", "statement type error!");
			break;
		}
	}

	/************************************************************/
	/* ������ Expr */
	/* �� �� �ú���������ʽ�ķ��� */
	/* ˵ �� ���ʽ����������ص��Ǽ��������������������ԣ� */
	/* ����ʽ�����͡����в���Ekind������ʾʵ���Ǳ�� */
	/* ����ֵ�Ρ� */
	/************************************************************/
	public TypeIR Expr(treeNode t) {
		
		TypeIR Eptr0 = null;
		TypeIR Eptr1 = null;
		TypeIR Eptr = null;
		
		if (t != null)
			switch (t.kind.exp) {
			case ConstK:
				switch (t.attr.expAttr.type) {
				case Int:
					Eptr = DecToPtr(t, DecKind.IntK);
					Eptr.kind = TypeKind.intTy;
					break;
				case Char:
					Eptr = DecToPtr(t, DecKind.CharK);
					Eptr.kind = TypeKind.charTy;
					break;
				default:
					ErrorPrompt(t.lineno, "", "this type is not for const!");
					break;
				}
				break;
			case VariK:
				/* Var = id������ */
				if (t.child[0] == null) {
					/* �ڷ��ű��в��Ҵ˱�ʶ�� */
					SymbTable table = findTable(t.lineno ,t.name[0]);
					t.table[0] = table;

					if (table.attrIR.kind != IdKind.varKind) {
						ErrorPrompt(t.lineno, t.name[0], "is not variable error!");
						Eptr = null;
					} else
						Eptr = table.attrIR.idtype;
				} 
				else if (t.attr.expAttr.varKind == VarKind.ArrayMembV)/* Var = Var0[E]������ */
						Eptr = arrayVar(t);
				else if (t.attr.expAttr.varKind == VarKind.FieldMembV)/* Var = Var0.id������ */
						Eptr = structVar(t);
				break;
			case OpK:
				/* �ݹ���ö��ӽڵ� */
				Eptr0 = Expr(t.child[0]);
				if (Eptr0 == null)
					return null;
				Eptr1 = Expr(t.child[1]);
				if (Eptr1 == null)
					return null;
				
				/* �����б� */
				if (Eptr0.kind==Eptr1.kind)
					switch (t.attr.expAttr.op) {
					case LT:
					case GT:
					case LE:
					case GE:
					case EQ:
					case NEQ:
						Eptr = BaseTypeToPtr(TypeKind.boolTy);
						break; /* �������ʽ */
					case PLUS:
					case MINUS:
					case TIMES:
					case OVER:
						Eptr = BaseTypeToPtr(TypeKind.intTy);
						break; /* �������ʽ */
					default:
						break;
					}
				else
					ErrorPrompt(t.lineno, "", "operator is not compat!");
				break;
			}
		return Eptr;
	}

	/************************************************************/
	/* ������ arrayVar */
	/* �� �� �ú�����������������±���� */
	/* ˵ �� ���var := var0[E]��var0�ǲ����������ͱ�����E�ǲ� */
	/* �Ǻ�������±��������ƥ�䡣 */
	/************************************************************/
	public TypeIR arrayVar(treeNode t) {

		TypeIR Eptr = null;

		/* �ڷ��ű��в��Ҵ˱�ʶ�� */
		SymbTable table = findTable(t.lineno, t.name[0]);
		t.table[0] = table;
		
		/* Var0���Ǳ��� */
		if (table.attrIR.kind != IdKind.varKind)
			ErrorPrompt(t.lineno, t.name[0], "is not variable error!");
		/* Var0�����������ͱ��� */
		else if (table.attrIR.idtype != null)
			if (table.attrIR.idtype.kind != TypeKind.arrayTy)
				ErrorPrompt(t.lineno, t.name[0], "is not array variable error !");
			else {
				/* ���E�������Ƿ����±�������� */
				TypeIR Eptr0 = table.attrIR.idtype.more.arrayAttr.indexTy;
				if (Eptr0 == null)
					return null;
				TypeIR Eptr1 = Expr(t.child[0]);// intPtr;
				if (Eptr1 == null)
					return null;
				if (Eptr0.kind==Eptr1.kind)
					Eptr = table.attrIR.idtype.more.arrayAttr.elemTy;
				else
					ErrorPrompt(t.lineno, "", "array member type must be int!");
			}
		return Eptr;
	}

	/************************************************************/
	/* ������ recordVar */
	/* �� �� �ú��������¼��������ķ��� */
	/* ˵ �� ���var:=var0.id�е�var0�ǲ��Ǽ�¼���ͱ�����id�� */
	/* ���Ǹü�¼�����е����Ա�� */
	/************************************************************/
	public TypeIR structVar(treeNode t) {
		
		boolean isFind = false;
		TypeIR Eptr = null;
		FieldChain currentP = null;

		/* �ڷ��ű��в��Ҵ˱�ʶ�� */
		SymbTable table = findTable(t.lineno, t.name[0]);
		t.table[0] = table;
		
		/* Var0���Ǳ��� */
		if (table.attrIR.kind != IdKind.varKind)
			ErrorPrompt(t.lineno, t.name[0], "is not a variable name!");
		/* Var0���Ǽ�¼���ͱ��� */
		else if (table.attrIR.idtype.kind != TypeKind.structTy)
			ErrorPrompt(t.lineno, t.name[0], "is not a struct variable name!");
		/* ���id�Ƿ��ǺϷ����� */
		else {
			for (int i=0;i<table.attrIR.idtype.more.structBody.size()&&!isFind;i++) {
				currentP = table.attrIR.idtype.more.structBody.get(i);
				isFind = t.child[0].name[0].equals(currentP.id);
				/* ������ */
				if (isFind)
					Eptr = currentP.unitType;
			}
			if (!isFind) {
				ErrorPrompt(t.child[0].lineno, t.child[0].name[0], "is not field type!");
				Eptr = null;
			}
			/* ���id���������,Var = Var0.id[]������ */
			else if (t.child[0].child[0] != null)
			{
				t = t.child[0];
				/* id�����������ͱ��� */
				if (Eptr.kind != TypeKind.arrayTy)
					ErrorPrompt(t.lineno, t.name[0], "is not array variable error !");
				else {
					/* ���E�������Ƿ����±�������� */
					TypeIR Eptr0 = Eptr.more.arrayAttr.indexTy;
					if (Eptr0 == null)
						return null;
					TypeIR Eptr1 = Expr(t.child[0]);// intPtr;
					if (Eptr1 == null)
						return null;
					if (Eptr0.kind == Eptr1.kind)
						Eptr = Eptr.more.arrayAttr.elemTy;
					else
						ErrorPrompt(t.lineno, "", "array member type must be int!");
				}
			}
		}
		return Eptr;
	}

	/************************************************************/
	/* ������ assignstatement */
	/* �� �� �ú�������ֵ������ */
	/* ˵ �� ��ֵ��������������ص��Ǽ�鸳ֵ�����˷������� */
	/* �������ԡ� */
	/************************************************************/
	public void AssignStmt(treeNode t) {
		
		SymbTable table = new SymbTable();
		TypeIR Eptr = null;
		treeNode child1 = t.child[0];
		treeNode child2 = t.child[1];
		
		if (child1.child[0] == null) {
			/* �ڷ��ű��в��Ҵ˱�ʶ�� */
			table = findTable(t.lineno, child1.name[0]);

			if (table.attrIR.kind != IdKind.varKind) {
				ErrorPrompt(child1.lineno, child1.name[0],
						"is not variable error!");
				Eptr = null;
			} else {
				Eptr = table.attrIR.idtype;
				child1.table[0] = table;
			}
		} else
		{	/* Var0[E]������ */
			if (child1.attr.expAttr.varKind == VarKind.ArrayMembV)
				Eptr = arrayVar(child1);
			else /* Var0.id������ */
			if (child1.attr.expAttr.varKind == VarKind.FieldMembV)
				Eptr = structVar(child1);
		}
		if (Eptr != null) {
			if ((t.nodeKind == NodeKind.StmtK)
					&& (t.kind.stmt == StmtKind.AssignK)) {
				/* ����ǲ��Ǹ�ֵ������ ���͵ȼ� */
				if (Expr(child2).kind!=Eptr.kind)
					ErrorPrompt(t.lineno, "", "assign expression's type is not compat!");
			}
			/* �����Եľ����ԣ���ֵ����в��ܳ��ֺ������� */
		}
	}

	/************************************************************/
	/* ������ callstatement */
	/* �� �� �ú������������������� */
	/* ˵ �� ����������������������ȼ����ű������������ */
	/* ��Param���֣��βη��ű����ַ��������������β� */
	/* ��ʵ��֮��Ķ�Ӧ��ϵ�Ƿ���ȷ�� */
	/************************************************************/
	public void CallStmt(treeNode t) {
		
		treeNode p = null;

		/* ��id����������ű� */
		SymbTable table = findTable(t.lineno, t.child[0].name[0]);
		t.child[0].table[0] = table;

		/* id���Ǻ����� */
		if (table.attrIR.kind != IdKind.funcKind)
			ErrorPrompt(t.lineno, t.name[0], "is not function name!");
		else/* ��ʵ��ƥ�� */
		{
			p = t.child[1];
			/* paramPָ���βη��ű��һԱ*/
			ParamTable paramP;
			for (int i=0; i<table.attrIR.more.funcAttr.param.size(); i++) {
				/*ʵ�θ��������β�*/
				if(p == null)
					ErrorPrompt(t.child[1].lineno, "", "param num is not match!");
				paramP = table.attrIR.more.funcAttr.param.get(i);
				SymbTable paraTable = paramP.table;
				TypeIR Etp = Expr(p);/* ʵ�� */
				/* �������Ͳ�ƥ�� */
				if (paraTable.attrIR.idtype.kind != Etp.kind)
					ErrorPrompt(p.lineno, "", "param type is not match!");
				p = p.sibling;
			}
			/*�βθ�������ʵ��*/
			if (p != null)
				ErrorPrompt(t.child[1].lineno, "", "param num is not match!");
		}
	}

	/************************************************************/
	/* ������ ifstatement */
	/* �� �� �ú����������������� */
	/* ˵ �� �����﷨�����������ӽڵ� */
	/************************************************************/
	public void IfStmt(treeNode t) {
		TypeIR Etp = Expr(t.child[0]);
		if (Etp != null)
			/* �����������ʽ */
			if (Etp.kind != TypeKind.boolTy)
				ErrorPrompt(t.lineno, "", "condition expressrion error!"); /* �߼����ʽ���� */
			else {
				/* ����then������в��� */
				Body(t.child[1]);
				/* ����else������в��� */
				Body(t.child[2]);
			}
	}

	/************************************************************/
	/* ������ whilestatement */
	/* �� �� �ú�������ѭ�������� */
	/* ˵ �� �����﷨�����������ӽڵ� */
	/************************************************************/
	public void WhileStmt(treeNode t) {
		TypeIR Etp = Expr(t.child[0]);
		if (Etp != null)
			/* �����������ʽ���� */
			if (Etp.kind != TypeKind.boolTy)
				ErrorPrompt(t.lineno, "", "condition expression error!"); /* �߼����ʽ���� */
			else /* ����ѭ������ */
				Body(t.child[1]);
	}
	
	/************************************************************/
	/* ������ forstatement */
	/* �� �� �ú�������ѭ�������� */
	/* ˵ �� �����﷨�����������ӽڵ� */
	/************************************************************/
	public void ForStmt(treeNode t) {
		TypeIR Etp = Expr(t.child[1]);
		if (Etp != null)
			/* �����������ʽ���� */
			if (Etp.kind != TypeKind.boolTy)
				ErrorPrompt(t.lineno, "", "condition expression error!"); /* �߼����ʽ���� */
			else {
				/* �����ʼ������ */
				Body(t.child[0]);
				/* ����ѭ�����Ʋ��� */
				Body(t.child[2]);
				/* ����ѭ���岿�� */
				Body(t.child[3]);
			}
	}

	/************************************************************/
	/* ������ readstatement */
	/* �� �� �ú����������������� */
	/* ˵ �� �����﷨���ڵ㣬�����������������Ƿ�Ϊ�������� */
	/************************************************************/
	public void CinStmt(treeNode t) {
		SymbTable table = findTable(t.lineno, t.name[0]);
		t.table[0] = table;

		/* ���Ǳ�����ʶ������ */
		if (table.attrIR.kind != IdKind.varKind)
			ErrorPrompt(t.lineno, t.name[0], "is not var name!");
	}

	/************************************************************/
	/* ������ writestatement */
	/* �� �� �ú���������������� */
	/* ˵ �� �����������еı��ʽ�Ƿ�Ϸ� */
	/************************************************************/
	public void CoutStmt(treeNode t) {
		TypeIR Etp = Expr(t.child[0]);
		if (Etp != null)
			/* ������ʽ����Ϊbool���ͣ����� */
			if (Etp.kind == TypeKind.boolTy)
				ErrorPrompt(t.lineno, "", "exprssion type error!");
	}

	/************************************************************/
	/* ������ returnstatement */
	/* �� �� �ú������������������� */
	/* ˵ �� ����������������Ƿ����������г��� */
	/************************************************************/
	public void ReturnStmt(treeNode t) {
		if (Level == 0)
			/* ��������������ں������⣬���� */
			ErrorPrompt(t.lineno, "", "return statement error!");
	}

	/************************************************************/
	/* ������ analyze */
	/* �� �� �ú��������ܵ�������� */
	/* ˵ �� ���﷨�����з��� */
	/************************************************************/
	public void run() {
		
		treeNode p = null;
	//	treeNode root = myParse.run();
		treeNode root = myParseLL1.run();
		
		/* ����﷨�������һ�������Ƿ�Ϊmain����  */
		if(root.child[0] != null)
			for(p = root.child[0]; p.sibling!=null; p = p.sibling);
		else
			ErrorPrompt(0, "", "no main function in syntax tree!");

			if(!p.name[0].equals("main"))
				ErrorPrompt(p.lineno, "", "no main function in syntax tree!");
		
		/* �������ű� */
		EnterNextLevel();

		/* ���﷨����һ����俪ʼ���� */
		p = root.child[0];
		while (p != null) {
			switch (p.nodeKind) {
			case TypeK:
				TypeDecPart(p);
				break;
			case DecK:
				VarDecPart(p);
				break;
			case FuncDecK:
				FuncDecPart(p);
				break;
			default:
				ErrorPrompt(p.lineno, "", "no this node kind in syntax tree!");
				break;
			}
			p = p.sibling;/* ѭ������ */
		}

		/* ���������ű� */
		PrintSymbTable();

		/* ���������� */
		for(int i=0;i<scope.size();i++)
			for(int j=0;j<scope.get(i).size();j++)
				if(scope.get(i).get(j).attrIR.kind==IdKind.typeKind)
					if(scope.get(i).get(j).attrIR.idtype.kind==TypeKind.structTy)
						PrintFieldChain(scope.get(i).get(j));
		
		/* �������ű� */
		if (Level != -1)
			ReturnLastLevel();
	}
}