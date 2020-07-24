package com.luxf.thread.queue;

import java.util.*;

/**
 * TODO: 注意、如果优先队列中的元素被更改,但是没有先移除, 再添加、优先队列的顺序,无法保证第一个是最小/最大的、
 * TODO: 通过更改前移除,更改后再添加的方式, 对优先队列的顺序进行重排, 否则无法保证优先队列的第一个元素是最小/最大的！
 * <p>
 * 优先队列执行add()系列方法都会对队列进行<B>重排</B>
 *
 * @author 小66
 * @date 2020-07-24 21:05
 **/
public class PriorityQueueDemo {

    /**
     * 构建Graph时,可以不需要Edge、 只需要点也可以表示、
     */
    private static class DirectedGraph {
        /**
         * 顶点的集合V
         */
        private List<Vertex> vertexList;
    }

    /**
     * TODO: 顶点、需要有个入度的属性(拓扑排序)、
     * 从入度为0的顶点开始、将之标记删除，然后将与该顶点相邻接的顶点的入度减1，再继续寻找入度为0的顶点，直至所有的顶点都已经标记删除或者图中有环。
     */
    private static class Vertex {
        // 入度、
        private int inDegree = 0;
        // 顶点的标识、name
        private String vertexName;

        // 是否被访问过的标记、用于BFS和DFS、
        private boolean visited = false;

        // 该点与目标终点的距离、用于BFS和DFS、
        private int dist = Integer.MAX_VALUE;

        // 具体的访问路径,该点指向当前对象、(当前点的上一个点) 用于BFS和DFS、
        private Vertex preVertex;

        Vertex(String vertexName) {
            this.vertexName = vertexName;
        }

        /**
         * 换种方式构建图, Graph中不需要维护Edge、
         * TODO: 维护一个邻接点的List、
         */
        private List<Vertex> adjacentList = new LinkedList<>();
        // 维护边的权重：点的名称为Key, 权重为Value
        private Map<String, Integer> adjacentWeightMap = new HashMap<>();

        // 无权、
        void addAdjacentVertex(Vertex vertex) {
            adjacentList.add(vertex);
        }

        // 赋权、
        void addAdjacentVertex(Vertex vertex, int weight) {
            adjacentList.add(vertex);
            adjacentWeightMap.put(vertex.vertexName, weight);
        }
    }

    /**
     * 贪心算法的精髓之处, 便是使用优先队列获取最小元素、
     */
    private static String dijkstraAlgorithm(DirectedGraph graph, Vertex startVertex, Vertex endVertex) {
        // 起点距离初始化为0、(因为起点离自己的最短距离就是0)
        startVertex.dist = 0;
        // TODO: 使用优先队列, 获取最小距离的顶点、
        PriorityQueue<Vertex> priorityQueue = new PriorityQueue<>(Comparator.comparing(v -> v.dist, Comparator.nullsLast(Integer::compareTo)));
        priorityQueue.addAll(graph.vertexList);
        // 直到所有的顶点都被标记为visited = true, 就会跳出循环、
        while (priorityQueue.size() > 0) {
            // 获取所有 visited = false的顶点, 并且 dist 最小的顶点、
            Vertex vertex = priorityQueue.peek();
            vertex.visited = true;
            System.out.println("vertex.vertexName = " + vertex.vertexName);
            vertex.adjacentList.forEach(v -> {
                if (!v.visited) {
                    int dist = vertex.dist + getEdgeWeight(vertex, v);
                    // 调整最小值, 更新实际路径点、
                    if (dist < v.dist) {
                        /**
                         * TODO: 通过更改前移除,更改后再添加的方式, 对优先队列的顺序进行重排, 否则无法保证优先队列的第一个元素是最小/最大的！
                         */
                        priorityQueue.remove(v); // TODO: 如果优先队列中的元素被更改,但是不先移除, 再添加、优先队列的顺序,无法保证第一个是最小/最大的、
                        v.dist = dist;
                        v.preVertex = vertex;
                        priorityQueue.offer(v);
                    }
                }
            });
            // 移除并重新排序、因为优先队列执行poll()后, 就会重新排序、
            // 为啥优先队列移除V1后(此时v2.dist > v4.dist), V2排在V4前面? 因为序列的值被更改后,没有重新排序。
            priorityQueue.poll();
        }

        // TODO: 经过贪心算法后, 起点到每个点的最短路径都已求出、
        graph.vertexList.forEach(vertex -> System.out.println(vertex.vertexName + " dist = " + vertex.dist));

        System.out.println("endVertex.dist = " + endVertex.dist);

        StringBuilder builder = new StringBuilder();
        while (endVertex != null) {
            builder.append(endVertex.vertexName);
            endVertex = endVertex.preVertex;
            if (endVertex != null) {
                builder.append(" -> ");
            }
        }
        System.out.println("path = " + builder.toString());
        return builder.toString();
    }

    private static Integer getEdgeWeight(Vertex vertex, Vertex adjacentVertex) {
        return vertex.adjacentWeightMap.get(adjacentVertex.vertexName);
    }

    private static DirectedGraph buildGraph(List<Vertex> vertexList) {
        DirectedGraph graph = new DirectedGraph();
        graph.vertexList = vertexList;
        return graph;
    }

    public static void main(String[] args) {
        List<Vertex> vertexList = new ArrayList<>();
        Vertex v1 = new Vertex("V1");
        Vertex v2 = new Vertex("V2");
        Vertex v3 = new Vertex("V3");
        Vertex v4 = new Vertex("V4");
        Vertex v5 = new Vertex("V5");
        Vertex v6 = new Vertex("V6");
        Vertex v7 = new Vertex("V7");

        // 利用邻接点集合构建图、不需要使用边构建图、
        v1.addAdjacentVertex(v4, 1);
        v1.addAdjacentVertex(v2, 2);
        v2.addAdjacentVertex(v4, 3);
        v2.addAdjacentVertex(v5, 10);
        v3.addAdjacentVertex(v1, 4);
        v3.addAdjacentVertex(v6, 5);
        v4.addAdjacentVertex(v3, 2);
        v4.addAdjacentVertex(v5, 2);
        v4.addAdjacentVertex(v6, 8);
        v4.addAdjacentVertex(v7, 4);
        v5.addAdjacentVertex(v7, 6);
        v7.addAdjacentVertex(v6, 1);

        vertexList.add(v1);
        vertexList.add(v2);
        vertexList.add(v3);
        vertexList.add(v4);
        vertexList.add(v5);
        vertexList.add(v6);
        vertexList.add(v7);

        DirectedGraph onlyVertexGraph = buildGraph(vertexList);
        dijkstraAlgorithm(onlyVertexGraph, v1, v6);
    }
}
