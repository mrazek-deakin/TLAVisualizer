package tlc2.tool;

import tla2sany.semantic.ExprNode;
import tla2sany.semantic.ExprOrOpArgNode;
import tla2sany.semantic.LabelNode;
import tla2sany.semantic.LetInNode;
import tla2sany.semantic.OpApplNode;
import tla2sany.semantic.OpDefNode;
import tla2sany.semantic.Subst;
import tla2sany.semantic.SubstInNode;
import tla2sany.semantic.SymbolNode;
import tlc2.output.EC;
import tlc2.util.Context;
import tlc2.util.Vect;
import tlc2.value.LazyValue;
import util.Assert;
import tla2sany.semantic.ASTConstants;
/**
 * This class attempts to recover all variables that
 * appear in (possibly nested) tuples in the subscript
 * of the next-state relation. Variables that appear
 * in other kinds of expressions are ignored.
 * The idea is to make sure that the subscript is a
 * tuple that contains at least the declared variables
 * of this specification object -- otherwise TLC's
 * analysis is incorrect.
 **/
class SubscriptCollector
{
	Spec spec;
    Vect components;

    SubscriptCollector(Spec spec)
    {
        this.components = new Vect();
        this.spec = spec;
    }

    void enter(ExprNode subscript, Context c)
    {
        // if it's a variable, add it to the vector and return
        SymbolNode var = spec.getVar(subscript, c, false);
        if (var != null)
        {
            components.addElement(var);
            return;
        }
        // otherwise further analyze the expression
        switch (subscript.getKind()) {
		case ASTConstants.OpApplKind: {
            OpApplNode subscript1 = (OpApplNode) subscript;
            SymbolNode opNode = subscript1.getOperator();
            ExprOrOpArgNode[] args = subscript1.getArgs();
            int opCode = BuiltInOPs.getOpCode(opNode.getName());
            // if it's a tuple, recurse with its members
            if (opCode == spec.OPCODE_tup)
            {
                for (int i = 0; i < args.length; i++)
                {
                    this.enter((ExprNode) args[i], c);
                }
                return;
            }
            // all other built-in operators are ignored
            else if (opCode != 0)
            {
                return;
            }
            // user-defined operator: look up its definition
            Object opDef = spec.lookup(opNode, c, false);
            if (opDef instanceof OpDefNode)
            {
                OpDefNode opDef1 = (OpDefNode) opDef;
                this.enter(opDef1.getBody(), spec.getOpContext(opDef1, args, c, false));
                return;
            }
            if (opDef instanceof LazyValue)
            {
                LazyValue lv = (LazyValue) opDef;
                this.enter((ExprNode) lv.expr, lv.con);
                return;
            }
            // ignore overridden operators etc
            break;
        }
        case ASTConstants.SubstInKind: {
            SubstInNode subscript1 = (SubstInNode) subscript;
            Subst[] subs = subscript1.getSubsts();
            Context c1 = c;
            for (int i = 0; i < subs.length; i++)
            {
                c1 = c1.cons(subs[i].getOp(), spec.getVal(subs[i].getExpr(), c, false));
            }
            this.enter(subscript1.getBody(), c1);
            return;
        }
        case ASTConstants.LetInKind: { // a bit strange, but legal...
            // note: the let-bound values become constants
            // so they are uninteresting for our purposes
            LetInNode subscript1 = (LetInNode) subscript;
            this.enter(subscript1.getBody(), c);
            return;
        }
            /***********************************************************
            * LabelKind case added by LL on 13 Jun 2007.               *
            ***********************************************************/
        case ASTConstants.LabelKind: { // unlikely, but probably legal...
            LabelNode subscript1 = (LabelNode) subscript;
            this.enter((ExprNode) subscript1.getBody(), c);
            /*******************************************************
            * Cast to ExprNode added by LL on 19 May 2008 because  *
            * of change to LabelNode class.                        *
            *******************************************************/
            return;
        }
        default: // give up
            Assert.fail(EC.TLC_CANT_HANDLE_SUBSCRIPT, subscript.toString());
            return;
        }
    }

    Vect getComponents()
    {
        return components;
    }
}
