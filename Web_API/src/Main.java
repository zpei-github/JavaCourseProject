import java.io.*;
import java.util.*;
import java.time.*;

public class Main {
    public static void main(String[] args) {
        String path1 = "output/output.csv";
        String path2 = "output/output_2.csv";
        String path3 = "output/output_3.csv";

        CreateAKGraphImpl c1 = new CreateAKGraphImpl(path1, path2, path3);
        /*String[] keys = {"Twitter", "Analytics", "SEO"};
        for(String s: c1.getBestCombination(keys)){
            System.out.println(s);
        }
         */
        System.out.println();
        System.out.println("【Experiment Start】");
        System.out.println();
        Exp(c1, path3);
        System.out.println();
        System.out.println("【Experiment End】");
    }
    public static void Exp(CreateAKGraphImpl bestComb, String path_mas) {
        // path_tes(t)为需要读入功能的文件，其格式为每行一组keys
        // path_ans(wer)为实际生产中使用的apis组合标准解答，其格式为每行一组apis
        // 每遍历一行path_tes寻找一次需要的apis组合
        Set<String> set_Key_tes = new HashSet<>();  // 待输入功能组
        Set<String> set_API_ans = new HashSet<>();  // 标准API组
        Set<String> set_API_res ;  // 结果API组
        Set<String> set_Key_ans = new HashSet<>();  // 结果API组覆盖的功能
        LocalTime startTime, endTime;

        String line_tes;
        String line_ans;

        int hitCount = 0;
        int tryCount = 0;  // 记录搜索API次数
        int tryLimit = 600;
        float hitRate = 0;  // 记录命中标准API次数

        float durationSum = 0;
        float avgDuration = 0;

        try (BufferedReader br_mas = new BufferedReader(new FileReader(path_mas))) {
            for (int i = 0; i < 800; i++) {
                br_mas.readLine(); // 跳过前400行
            }
            while ((line_ans = br_mas.readLine()) != null) {
                if (tryCount >= tryLimit){
                    break;
                }
                startTime = LocalTime.now();

                System.out.println("------------   第" + (tryCount+1) + "轮测试" + " 开始   --------------");
                // 处理第一行（line_ans）
                String[] String_ans = line_ans.split(",");
                set_API_ans.addAll(Arrays.asList(String_ans));

                // 读取下一行（line_ans）
                if ((line_tes = br_mas.readLine()) != null) {
                    // 处理第二行（line_ans）以及完成一次比较

                    String[] String_tes = line_tes.split(",");
                    set_Key_tes.addAll(Arrays.asList(String_tes));

                    System.out.print("需求keys：\t");
                    for(String s: set_Key_tes){
                        System.out.print(s + ", ");
                    }
                    System.out.println();
                    // 每输入一个字符串集合set_tes，结果返回一个字符串集合set_res，与字符串集合set_ans比较
                    set_API_res = bestComb.getBestCombination(String_tes);
                    endTime = LocalTime.now();

                    System.out.print("标准apis：\t");
                    for(String s: String_ans){
                        System.out.print(s + ", ");
                    }
                    System.out.println();

                    System.out.print("结果apis：\t");
                    for (String api: set_API_res){
                        System.out.print(api + ", ");
                    }
                    boolean isEqual = set_API_ans.containsAll(set_API_res);
                    if (isEqual){
                        hitCount++;
                    }
                    System.out.println();
                    Duration duration = Duration.between(startTime, endTime);
                    long timeCost = duration.toSeconds();
                    durationSum += timeCost;
                    System.out.println("匹配："+isEqual);
                    System.out.println("耗时：" + duration);
                    System.out.println("------------   第" + (tryCount+1) + "轮测试" + " 结束   --------------");                    System.out.println();
                    tryCount++;
                } else {
                    // 如果没有第二行，处理相应逻辑（例如，结束循环）
                    System.out.println("没有第二行");
                    break;
                }
                set_API_ans.clear();
                set_Key_tes.clear();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        hitRate = (float)hitCount/(float)tryCount;
        avgDuration = durationSum/(float)tryCount;

        System.out.println("【 Hit Rate: "+hitRate*100+"% 】");
        System.out.println("【 Average Computation time: "+ avgDuration +"s 】");

    }
}

// 图的节点类
class GraphNode {
    private String name;
    public final Integer label;  //0是api, 1是key
    private Map<GraphNode, Long> neighbors;
    public GraphNode(Integer label){
        this.label = label;
    }
    public GraphNode(String name, Integer label) {
        this.name = name;
        this.label = label;
        this.neighbors = new HashMap<>();
    }
    public  int get_degree(){
        return neighbors.size();
    }
    @Override
    public int hashCode() {// 获取对象标识Hash码
        return Objects.hash(this.name, label); // 获取Hash码
    }
    @Override
    public boolean equals(Object o){
        if (o == null || getClass() != o.getClass())// 对象类型不匹配
            return false;// 对象不同
        GraphNode o1 = (GraphNode) o;
        return this.name.equals(o1.name) && (Objects.equals(this.label, o1.label));
    }

    //禁止直接获取
    public Map<GraphNode, Long> getNeighborMap(){
        return this.neighbors;
    }
    //返回是否为邻居
    public boolean isNeighbor(GraphNode node){
        return this.neighbors.containsKey(node);
    }
    public Set<GraphNode> getNeighbors(){
        return this.neighbors.keySet();
    }
    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }
}
//API图功能
interface IAPIGraph{
    //根据功能需求组合，返回关键词结点数组
    public GraphNode[] getKeyNodes(String[] keys);

    //返回图中所有节点
    public List<GraphNode> getNodes();

    //向API&Key图中添加API结点
    public void addAPI(String name);

    //向API&Key图中添加API结点
    public void addAPI(GraphNode apiNode) throws Exception;

    //向API&Key图中添加Key结点
    public void addKey(String name);

    //向API&Key图中添加Key结点
    public void addKey(GraphNode keyNode) throws Exception;

    //连接API&Key中两个点并赋权值
    public void link(GraphNode startNode, GraphNode endNode, Long weight) throws Exception;

    //修改两点之间的权值
    public void updateWeight(GraphNode node1, GraphNode node2, Long weight) throws Exception;

    //找出两个顶点之间边的权重, 如果没有连接，则权重为null
    public Long getWeight(GraphNode node1, GraphNode node2) throws Exception;

    //根据名称和label找出对应节点，没有就返回null
    public GraphNode findNode(String name, Integer label);

    //判断图中是否存在该结点
    public boolean isContain(GraphNode node);

    //获取某个api的所有关键词
    public Set<GraphNode> get_keys(GraphNode node) throws Exception;

    //找出key的APIs，需要提前判断是否为key
    public Set<GraphNode> get_APIs(GraphNode node) throws Exception;

    //协作次数转换为权重
    public void weightTransfer();
}

// 图类
class AKGraph implements IAPIGraph{
    /*
     * AKGraph
     * 关键词和api抽象为相同顶点
     * api之间的权重根据mashup内的协作次数计算
     * api和关键词之间的权重设定为Integer.MAX_VALUE
     *
     * */

    //储存API结点和keys结点
    Map<String, GraphNode> nodes;
    //图的边
    public AKGraph() {
        this.nodes = new HashMap<>();
    }
    @Override
    public void addAPI(String name) {
        nodes.put(name, new GraphNode(name, 0));
    }
    @Override
    public void addKey(String name) {
        nodes.put(name + "key", new GraphNode(name, 1));
    }
    public GraphNode findNode(String name, Integer label){
        if(Objects.equals(label, 0)){
            return nodes.get(name);
        }
        if(Objects.equals(label,1)){
            return nodes.get(name + "key");
        }
        return null;
    }
    public boolean isContain(GraphNode node){
        return findNode(node.getName(), node.label) != null;
    }
    //返回图中所有节点
    public List<GraphNode> getNodes(){
        List<GraphNode> nodeList = new ArrayList<>(this.nodes.size());
        //遍历Map的entrySet()，获取所有结点
        for(Map.Entry<String, GraphNode> entry: this.nodes.entrySet()){
            nodeList.add(entry.getValue());
        }
        return nodeList;
    }
    public void addAPI(GraphNode apiNode) throws Exception{
        if(apiNode == null){
            throw new NullPointerException("空值顶点");
        }
        if(Objects.equals(apiNode.label,0)){
            throw new UnsupportedOperationException("【addAPI】<" + apiNode.getName() + " " + apiNode.label+"> 没有添加到图中");
        }
        nodes.put(apiNode.getName(), apiNode);
    }
    //向API&Key图中添加Key结点
    public void addKey(GraphNode keyNode)throws Exception{
        if(keyNode == null){
            throw new NullPointerException("空值顶点");
        }
        if(Objects.equals(keyNode.label, 1)){
            throw new UnsupportedOperationException("【addKey】<" + keyNode.getName() + " " + keyNode.label+"> 没有添加到图中");
        }
        nodes.put(keyNode.getName(), keyNode);
    }

    @Override
    public GraphNode[] getKeyNodes(String[] keys) {
        GraphNode[] nodes = new GraphNode[keys.length];
        for (int i = 0; i < keys.length; i++){
            nodes[i] = findNode(keys[i], 1);
        }
        return nodes;
    }
    @Override
    public void link(GraphNode startNode, GraphNode endNode, Long weight) throws Exception{
        if(startNode == null || endNode == null){
            throw new NullPointerException("空值顶点");
        }
        if(!this.isContain(startNode) || !this.isContain(endNode)){
            throw new UnsupportedOperationException("【updateWeight】\n <"
                    + startNode.getName() + " " + endNode.label+">\n"
                    + "<" + startNode.getName() + " " + endNode.label  +"> \n"
                    + " 没有添加到图中");
        }

        //无向图进行双向连接，如果单向图则可以只连接单向
        startNode.getNeighborMap().put(endNode, weight);
        endNode.getNeighborMap().put(startNode, weight);
    }

    //更新图中的权重
    public void updateWeight(GraphNode startNode, GraphNode endNode, Long weight) throws Exception{
        if(startNode == null || endNode == null){
            throw new NullPointerException("空值顶点");
        }
        if(!this.isContain(startNode) || !this.isContain(endNode)){
            throw new UnsupportedOperationException("【updateWeight】\n <"
                    + startNode.getName() + " " + startNode.label+">\n"
                    + "<" + endNode.getName() + " " + endNode.label  +"> \n"
                    + " 没有添加到图中");
        }
        if(startNode.isNeighbor(endNode) && endNode.isNeighbor(startNode)) {
            //无向图更新，如果是有向图，则只需要更新单向权重
            startNode.getNeighborMap().replace(endNode, startNode.getNeighborMap().get(endNode) + weight);
            endNode.getNeighborMap().replace(startNode, endNode.getNeighborMap().get(startNode) + weight);
        }else{
            throw new UnsupportedOperationException("【updateWeight】\n <"
                    + startNode.getName() + " " + startNode.label+">\n"
                    + "<" + endNode.getName() + " " + endNode.label  +"> \n"
                    + " 更新非邻居结点的权重");
        }
    }

    //获取图中边的权重
    @Override
    public Long getWeight(GraphNode startNode, GraphNode endNode) throws Exception{
        if(startNode == null || endNode == null){
            throw new NullPointerException("【getWeight】 空值顶点");
        }
        if(!this.isContain(startNode) || !this.isContain(endNode)){
            throw new UnsupportedOperationException("【getWeight】\n <"
                    + startNode.getName() + " " + startNode.label+">\n"
                    + "<" + endNode.getName() + " " + endNode.label  +"> \n"
                    + " 没有添加到图中");
        }

        //无向图判断，如果是有向图可以更新
        if(startNode.isNeighbor(endNode) && endNode.isNeighbor(startNode)){
            if(Objects.equals(startNode.getNeighborMap().get(endNode), endNode.getNeighborMap().get(startNode))){
                return startNode.getNeighborMap().get(endNode);
            }else {
                System.out.println(startNode.getNeighborMap().get(endNode));
                System.out.println(endNode.getNeighborMap().get(startNode));
                throw new ArithmeticException("无向边双向权重不相等:\n <"
                        + startNode.getName() + " " + startNode.label+"> <---->"
                        + "<" + endNode.getName() + " " + endNode.label  +">");
            }
        }
        return null;
    }

    @Override
    public Set<GraphNode> get_keys(GraphNode node) throws Exception{
        if(node == null){
            throw new NullPointerException("【get_keys】空值顶点");
        }
        if(Objects.equals(node.label,1)){
            throw new UnsupportedOperationException("【get_keys】<" + node.getName() + " " + node.label+"> 不是api");
        }
        if(!this.isContain(node)){
            throw new UnsupportedOperationException("【get_keys】<" + node.getName() + " " + node.label+"> 没有添加到图中");
        }
        Set<GraphNode> keySet = new HashSet<>();
        for(GraphNode n: node.getNeighbors()){
            if(Objects.equals(n.label,1)){
                keySet.add(n);
            }
        }
        return keySet;
    }
    @Override
    public Set<GraphNode> get_APIs(GraphNode node) throws Exception{
        if(node == null){
            throw new NullPointerException("【get_APIs】空值顶点");
        }
        if(Objects.equals(node.label,0)){
            throw new UnsupportedOperationException("【get_keys】<" + node.getName() + " " + node.label+"> 不是key");
        }
        if(!this.isContain(node)){
            throw new UnsupportedOperationException("【get_APIs】<" + node.getName() + " " + node.label+"> 没有添加到图中");
        }
        Set<GraphNode> apiSet = new HashSet<>();
        for(GraphNode n: node.getNeighbors()){
            if(Objects.equals(n.label,0)){
                apiSet.add(n);
            }
        }
        return apiSet;
    }

    //协作次数转换为权重
    public void weightTransfer(){
        Long maxWeight = 0L;
        GraphNode[] graphNodes = new GraphNode[2];
        List<GraphNode> nodes = this.getNodes();
        try {
            for (GraphNode node1 : nodes) {
                for (GraphNode node2 : node1.getNeighbors()) {
                    if(Objects.equals(node1.label, 1) ||Objects.equals(node2.label, 1)){
                        continue;
                    }
                    Long w = getWeight(node1, node2);
                    if (w > maxWeight) {
                        maxWeight = w;
                        graphNodes[0] = node1;
                        graphNodes[1] = node2;
                    }
                }
            }

            // System.out.println("最大权值：" + maxWeight + " node1 " + graphNodes[0].getName() + " node2 " + graphNodes[1].getName());
            // System.out.println();

            Set<GraphNode> updatedNodes = new HashSet<>();
            for (GraphNode node1 : nodes) {
                if (Objects.equals(node1, null) || Objects.equals(node1.label, 1)) {
                    continue;
                }
                for (GraphNode node2 : node1.getNeighbors()) {
                    if (Objects.equals(node2.label, 1) || updatedNodes.contains(node2)) {
                        continue;
                    }
                    Long cw = getWeight(node1, node2);
                    updateWeight(node1, node2, maxWeight + 1 - cw);
                }
                updatedNodes.add(node1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void printGraph(){
        try {
            for (GraphNode node1 : this.getNodes()) {
                System.out.print("【head】" + "(" + node1.getName() + ": " + node1.label + ") -->");
                for (GraphNode node2 : node1.getNeighbors()) {
                    System.out.print("【node】" + "(" + node2.getName() + ": " + node2.label + ") weight =" + getWeight(node1, node2) + " -->");
                }
                System.out.println("NULL");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

interface ICreateAKGraph{
    public Long AK_WEIGHT = (long)Integer.MAX_VALUE;
    //读取csv文件，在这个函数内，直接完成建图
    public void createGraph(String path_cre, String path_lin, String path_upd);

    /*
     * 根据app和api协作关系完成图中的api部分
     *
     * 读取mashup中的Name(app)和Related APIs两列
     * Related APIs获取每个api名称，并向graph.nodes中添加节点
     * 再根据协作关系更新graph.edges
     * */
    public void createAPIPart(IAPIGraph graph, String[] app_l_apis);

    /*
     * 完成连接API和keys
     * 读取api.csv文件将已经建立的APIPart部分再添加keys部分
     * */
    public void link_API_Key(IAPIGraph graph, String[] api_l_keys);

    /*
     * 更新API和keys关系
     *
     * 再次遍历mashup.csv文件，根据表中的Categories列更新api和keys连接
     * */
    public boolean update_API_Key(IAPIGraph graph, String[] apis, String[] keys);
}

class CreateAKGraphImpl implements ICreateAKGraph {
    private final IAPIGraph graph;
    private NSTTree nstTree;

    public CreateAKGraphImpl(String path_mas_1, String path_lin, String path_mas_2){
        this.graph = new AKGraph();
        this.createGraph(path_mas_1, path_lin, path_mas_2);
    }
    public Set<String> getBestCombination(String[] keys){
        this.nstTree = new NSTTree(this.graph);
        return nstTree.getBestCombination(keys);
    }
    @Override
    public void createGraph(String path_mas_1, String path_lin, String path_mas_2){

        // 读取mashup.csv建立APIPart
        try (BufferedReader br_cre = new BufferedReader(new FileReader(path_mas_1))) {
            String line;
            while ((line = br_cre.readLine()) != null) {
                String[] app_l_apis = line.split(",");
                createAPIPart(this.graph, app_l_apis);
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + path_mas_1);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 读取api.csv连接api与其基础keys
        try (BufferedReader br_lin = new BufferedReader(new FileReader(path_lin))) {
            String line;
            while ((line = br_lin.readLine()) != null) {
                String[] api_l_keys = line.split(",");
                link_API_Key(this.graph, api_l_keys);
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + path_lin);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 再次读取mashup.csv更新剩余keys
        try (BufferedReader br_upd = new BufferedReader(new FileReader(path_mas_2))) {
            String line;
            String[] apis = null;  // 初始化数组
            int lineNumber = 1;
            // 这里要求csv文件格式为：奇数行为APIs，偶数行为Keys
            while ((line = br_upd.readLine()) != null) {
                if (lineNumber % 2 == 0) {
                    // 处理偶数行
                    String[] keys = line.split(",");
                    update_API_Key(this.graph, apis, keys);
                } else {
                    // 处理奇数行
                    apis = line.split(",");
                }
                lineNumber++;
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + path_lin);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        graph.weightTransfer();
    }
    @Override
    public void createAPIPart(IAPIGraph graph, String[] app_l_apis) {
        // 若不存在结点，则先新建结点添加到图中
        // 外层遍历
        for (int api_i = 1; api_i < app_l_apis.length; api_i++){
            if (graph.findNode(app_l_apis[api_i], 0) == null) {
                graph.addAPI(app_l_apis[api_i]);
                //System.out.println("added API【 " + app_l_apis[api_i]);
            }
            GraphNode node_i = graph.findNode(app_l_apis[api_i], 0);
            // 内层遍历
            for (int api_j = api_i+1; api_j < app_l_apis.length; api_j++){
                if (graph.findNode(app_l_apis[api_j], 0) == null){
                    graph.addAPI(app_l_apis[api_j]);
                    //System.out.println("added API【 " + app_l_apis[api_j]);
                }
                GraphNode node_j = graph.findNode(app_l_apis[api_j], 0);
                try {
                    if(!node_i.isNeighbor(node_j)){
                        // 若原本未两个结点未连接，则先建立连接
                        graph.link(node_i,node_j,1L);
                    }else{
                        // 若原本两个结点已连接，则权重（协作次数）加1
                        graph.updateWeight(node_i, node_j, 1L);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void link_API_Key(IAPIGraph graph, String[] api_l_keys) {
        // 若不存在结点，则先新建结点添加到图中
        try {
            if (graph.findNode(api_l_keys[0], 0) == null) {
                //graph.printGraph();
                graph.addAPI(api_l_keys[0]);
                //System.out.println("added API【" + api_l_keys[0]);
            }
            GraphNode curAPI = graph.findNode(api_l_keys[0], 0);
            for (int keys = 1; keys < api_l_keys.length; keys++) {
                try {
                    if (graph.findNode(api_l_keys[keys], 1) == null) {
                        graph.addKey(api_l_keys[keys]);
                        //System.out.println("added KEY】" + api_l_keys[keys]);
                    }
                    // 找到当前的Key结点，连接当前API与Key，并合理赋权
                    GraphNode curKey = graph.findNode(api_l_keys[keys], 1);
                    graph.link(curAPI, curKey, AK_WEIGHT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean update_API_Key(IAPIGraph graph, String[] apis, String[] keys){
        Set<String> apis_keys = new HashSet<>();
        List<GraphNode> apiNodes = new LinkedList<>();
        //获取给定的apis中所有api的keys集合
        try{
            for(String api: apis){
                GraphNode apiNode = graph.findNode(api, 0);
                if(!graph.isContain(apiNode)){
                    continue;
                }
                apiNodes.add(apiNode);

                Set<GraphNode> keyNodes = graph.get_keys(apiNode);
                for(GraphNode keyNode:keyNodes){
                    apis_keys.add(keyNode.getName());  //获取keys的名称
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            for (String key : keys) {
                if (!apis_keys.contains(key)) {
                    if ((graph.findNode(key, 1)) == null) {
                        graph.addKey(key);
                    }
                    GraphNode keyNode = graph.findNode(key, 1);
                    for (GraphNode node : apiNodes) {
                        graph.link(keyNode, node, AK_WEIGHT);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

}

class NSTTree {
    IAPIGraph akGraph; //已经建立的AK图
    List<GraphNode> nodes_index_map;  //AK图中nodes和index的映射
    List<List<VNode>> vNgraph;  //斯坦纳树图
    public NSTTree(IAPIGraph graph){
        this.akGraph = graph;
        this.nodes_index_map = akGraph.getNodes();
        this.AKGraph2Matrix();
    }
    //斯坦纳树结点
    static class VNode {
        int to;
        Long weight;
        VNode(int to, Long weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    //将AK图转换为斯坦纳树需要的图
    private void AKGraph2Matrix(){
        this.vNgraph = new ArrayList<>(this.nodes_index_map.size());
        try {
            for (int i = 0; i < this.nodes_index_map.size(); i++) {
                GraphNode akNode = this.nodes_index_map.get(i);
                Set<GraphNode> node_neighbor = akNode.getNeighbors();
                List<VNode> vnodes = new ArrayList<>(node_neighbor.size());
                for (GraphNode node : node_neighbor) {
                    int index = this.nodes_index_map.indexOf(node);
                    vnodes.add(new VNode(index, this.akGraph.getWeight(akNode, node)));
                }
                this.vNgraph.add(i, vnodes);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //返回api名称的集合
    public Set<String> getBestCombination(String[] keys){
        GraphNode[] keyNodes = this.akGraph.getKeyNodes(keys);
        Set<String> apis = new HashSet<>();
        if(keyNodes.length > 1) {
            int n = this.nodes_index_map.size();
            int k = keyNodes.length;
            int[] x = new int[k];
            for(int i = 0; i < k; i++){
                x[i] = this.nodes_index_map.indexOf(keyNodes[i]);
            }
            try {
                for (int num : getMultiKeyPath(n, k, x)) {
                    GraphNode node = nodes_index_map.get(num);
                    if(Objects.equals(node.label, 0)){
                        apis.add(node.getName());
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if(keyNodes.length == 1) {
            String aim_api = null;
            Long min_weight = Long.MAX_VALUE;
            try {
                for (GraphNode startnode: akGraph.get_APIs(keyNodes[0])) {
                    for(GraphNode endNode: startnode.getNeighbors()) {
                        Long curWeight = akGraph.getWeight(startnode, endNode);
                        if (curWeight < min_weight) {
                            aim_api = startnode.getName();
                            min_weight = curWeight;
                        }
                    }
                }
                apis.add(aim_api);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return apis;
    }

    //斯坦纳树核心代码
    private List<Integer> getMultiKeyPath(int n, int k, int[] x){
        int y = -1;
        long[][] dp = new long[n][1 << (k + 1)];
        List<Integer>[][] path = new ArrayList[n][1 << k];
        for (int i = 0; i < n; i++) {
            Arrays.fill(dp[i], Long.MAX_VALUE);
            for (int j = 0; j < (1 << k); j++) {
                path[i][j] = new ArrayList<>();
            }
        }

        for (int i = 0; i < k; i++) {
            dp[x[i]][1 << i] = 0;
            path[x[i]][1 << i].add(x[i]); // 路径记录时使用1-based索引
            y = x[i];
        }
        for (int s = 1; s < (1 << k); s++) {
            for (int i = 0; i < n; i++) {
                for (int t = s & (s - 1); t > 0; t = (t - 1) & s) {
                    if (dp[i][s] > dp[i][t] + dp[i][s ^ t]) {
                        dp[i][s] = dp[i][t] + dp[i][s ^ t];
                        path[i][s].clear();
                        mergePaths(path[i][s], path[i][t], path[i][s ^ t]);
                    }
                }
            }
            deal(s, n, dp, path, vNgraph);
        }
        return path[y][(1 << k) - 1];
    }
    private void deal(int s, int n, long[][] dp, List<Integer>[][] path, List<List<VNode>> graph) {
        PriorityQueue<long[]> pq = new PriorityQueue<>(Comparator.comparingLong(o -> o[1]));
        boolean[] vis = new boolean[n];

        for (int i = 0; i < n; i++) {
            if (dp[i][s] != Long.MAX_VALUE) {
                pq.add(new long[]{i, dp[i][s]});
            }
        }

        while (!pq.isEmpty()) {
            long[] tmp = pq.poll();
            int u = (int) tmp[0];
            if (vis[u]) continue;
            vis[u] = true;
            for (VNode edge : graph.get(u)) {
                int v = edge.to;
                long newWeight = tmp[1] + edge.weight;
                if (newWeight < dp[v][s]) {
                    dp[v][s] = newWeight;
                    pq.add(new long[]{v, dp[v][s]});
                    path[v][s].clear();
                    path[v][s].addAll(path[u][s]);
                    if (!path[v][s].contains(v)) { // 防止重复添加节点
                        path[v][s].add(v); // 添加节点，使用1-based索引
                    }
                }
            }
        }
    }
    // 合并两条路径，确保不会有重复节点
    private void mergePaths(List<Integer> mergedPath, List<Integer> path1, List<Integer> path2) {
        Set<Integer> set = new HashSet<>(path1);
        mergedPath.addAll(path1);
        for (int node : path2) {
            if (!set.contains(node)) {
                mergedPath.add(node);
            }
        }
    }
}