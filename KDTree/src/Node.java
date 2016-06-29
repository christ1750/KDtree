import java.util.List;

/**
 *author: christ
 *data: 2015年11月19日下午3:52:58
 *function:
 */

public class Node {
	public List<Integer[]> data;
	public int dim;
	public Node p;
	public Node lchild;
	public Node rchild;
	
	public Node(){}
	
	public Node(List<Integer[]> data,int dim){
		this.data = data;
		this.dim = dim;
		this.lchild = null;
		this.rchild = null;
	}
	public Node(List<Integer[]> data,Node lchild,Node rchild,Node p){
		this.data = data;
		this.lchild = lchild;
		this.rchild = rchild;
	}
}

