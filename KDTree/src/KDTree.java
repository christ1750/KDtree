import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 *author: christ
 *data: 2015年11月19日下午3:52:50
 *function:
 *
 *dim从1开始
 */

public class KDTree {
	private double min_dist;
	private Node close_Node;
	private Node root;
	/**数据的维度*/
	private int k;
	/**搜索路径*/
	private List<Node> searchPath;
	public KDTree(int k){
		this.k = k;
		root = null;
		searchPath = new ArrayList<Node>();
	}
	/**根据深度得到分割维度*/
	public int getDim(int depth){
		return (depth % k) + 1;
	}
	
	/**得到根节点*/
	public Node getRoot() {
		return root;
	}
	
	public double getMin_dist() {
		return min_dist;
	}
	public Node getClose_Node() {
		return close_Node;
	}
	/**找到切分点*/
	public int findCut(List<Integer[]>dataset,int dim){
		Iterator<Integer[]>it = dataset.iterator();
		List<Integer>temp = new ArrayList<Integer>();  //装维度,找中位数
		while(it.hasNext()){
			Integer[]data = it.next();
			temp.add(data[dim-1]);
		}
		Collections.sort(temp);
		return temp.get(temp.size()/2);
	}
	
	/**
	 * 
	 * @param dataset 数据集
	 * @return 三个节点的分类，根节点，左节点，右节点
	 */
	public List<Integer[]>[] getPartition(List<Integer[]>dataset,int dim){
		List<Integer[]>[] result = new ArrayList[3];
		
		List<Integer[]>root_dataset = new ArrayList<Integer[]>();
		List<Integer[]>r_dataset = new ArrayList<Integer[]>();
		List<Integer[]>l_dataset = new ArrayList<Integer[]>();
		int cutNum = findCut(dataset, dim);  //切分的值
		Iterator<Integer[]>it = dataset.iterator();
		while(it.hasNext()){
			Integer[] data = it.next();
			if(data[dim-1] > cutNum){
				r_dataset.add(data);
			}else if(data[dim-1] < cutNum){
				l_dataset.add(data);
			}else{
				root_dataset.add(data);
			}
		}
		result[0] = root_dataset;
		result[1] = l_dataset;
		result[2] = r_dataset;
		
		return result;
	}
	
	public void insert(List<Integer[]>dataset,int depth){
		int temp_depth = depth+1;
		List<Integer[]>[] result = getPartition(dataset, getDim(temp_depth)); //result[0]是当前要插入的节点
		Node p = root;
		Node node = new Node(result[0],getDim(temp_depth)); //要插入的节点
		for(int i = 1; i <= temp_depth; i++){
			int temp_dim = getDim(i);
			if(result[0].get(0)[temp_dim-1] < p.data.get(0)[temp_dim-1]){
				if(p.lchild == null){
					p.lchild = node;
					node.p = p;
					return;
				}
				else
					p = p.lchild;
			}else if(result[0].get(0)[temp_dim-1] > p.data.get(0)[temp_dim-1]){
				if(p.rchild == null){
					p.rchild = node;
					node.p = p;
					return;
				}
				else
					p = p.rchild;
			}
		}
	}
	/**递归创建KD树*/
	public void createKDT(List<Integer[]>dataset,int depth){
		int temp_depth = ++depth;
		List<Integer[]>[] result = getPartition(dataset, getDim(temp_depth));
		if(root == null){
			root = new Node(result[0],getDim(temp_depth));
		}
		if(result[1].size() == 0 && result[2].size() == 0)
			return;
		if(result[1].size() != 0){
			insert(result[1],temp_depth);
			createKDT(result[1],temp_depth);
		}
		if(result[2].size()!=0){
			insert(result[2],temp_depth);
			createKDT(result[2],temp_depth);
		}		
	}
	
	/**查找给点数组所在KDTree中的区域*/
	public Node findLeef(Integer[] a){
		Node p = root;
		int depth = 1;
		while(true){
			int dim = getDim(depth); //要比较的维度
			if(a[dim-1] < p.data.get(0)[dim-1]){
				if(p.lchild == null){
					return p;
				}else
					p = p.lchild;
			}else{
				if(p.rchild == null){
					return p;
				}else
					p = p.rchild;
			}
			depth++;
		}
	}
	/**给定根节点，遍历取出每一个节点*/
	public void otherSearchPath(Node p){
		if(p == null){
			return;
		}
		searchPath.add(p);
		if(p.lchild != null){
			otherSearchPath(p.lchild);
			
		}
		if(p.rchild != null){
			otherSearchPath(p.rchild);
		}
	}
	/**两点的距离*/
	public double cal_distance(Integer[] a, Integer[] b) throws Exception{
		double sum = 0.0;
		if(a.length != b.length)
			throw new Exception("数组长度不等");
		for(int i = 0; i < a.length; i++){
			sum += Math.pow(a[i]-b[i], 2);
		}
		return Math.sqrt(sum);
	}
	
	/**最近邻查找
	 * @throws Exception */
	public void findNearNode(Integer[] a) throws Exception{
		double min_distance = Double.MAX_VALUE;       //当前最近距离
		Node node = findLeef(a);       //找到包含该点的叶子节点
		Node temp_close = null;        //临时最近节点
		while(node != null){
			double temp_dist = cal_distance(a, node.data.get(0));
			if(temp_dist <= min_distance){
				min_distance = temp_dist;
				temp_close = node;
			}
			Integer[] temp_a = new Integer[a.length];
			temp_a = Arrays.copyOf(a, a.length);
			temp_a[node.dim-1] = node.data.get(0)[node.dim-1];  //计算圆与分割维度是否有交集
			if(min_distance > cal_distance(a, temp_a)){//说明有交集，需要分割节点的另一枝
				//查看目标节点在哪一支
				if(a[node.dim-1] < node.data.get(0)[node.dim-1]){//说明在左枝
					otherSearchPath(node.rchild);
				}else
					otherSearchPath(node.lchild);
				//查探另一支的所有节点
				Iterator<Node>it = searchPath.iterator();
				while(it.hasNext()){
					Node temp_node = it.next();
					if(min_distance > cal_distance(a, temp_node.data.get(0))){
						min_distance = cal_distance(a, temp_node.data.get(0));
						temp_close = temp_node;
					}
				}
				searchPath.clear();
				node = node.p;
			}else
				break;
		}
		min_dist = min_distance;
		close_Node = temp_close;
	}
	/**显示数组*/
	public void showList(List<Integer[]>list){
		Iterator<Integer[]>it = list.iterator();
		while(it.hasNext()){
			Integer[] data = it.next();
			System.out.print("{");
			for(int i = 0; i < data.length; i++){
				System.out.print(data[i] + " ");
			}
			System.out.print("}" + ",");
		}
	}
	/**先序遍历*/
	public void showKDT(Node p){
		showList(p.data);
		System.out.println();
		if(p.lchild != null)
			showKDT(p.lchild);
		if(p.rchild != null)
			showKDT(p.rchild);
	}
	
	public static void main(String[] args) throws Exception{
		List<Integer[]>dataset = new ArrayList<Integer[]>();
		dataset.add(new Integer[]{2,3});
		dataset.add(new Integer[]{5,4});
		dataset.add(new Integer[]{9,6});
		dataset.add(new Integer[]{4,7});
		dataset.add(new Integer[]{8,1});
		dataset.add(new Integer[]{7,2});
		KDTree tree = new KDTree(2);
		tree.createKDT(dataset, 0);
		//tree.showKDT(tree.getRoot());
		//Node p = tree.findLeef(new Integer[]{10,6});
		//tree.showList(p.data);
		tree.findNearNode(new Integer[]{2,2});
		Node nearNode = tree.getClose_Node();
		tree.showList(nearNode.data);
		System.out.println(tree.getMin_dist());
	}
}

