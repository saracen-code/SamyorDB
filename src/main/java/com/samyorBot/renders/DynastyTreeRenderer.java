package com.samyorBot.renders;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Node;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;

public class DynastyTreeRenderer {

    public static File renderTreeAsImage(Map<Long, List<Long>> tree, Set<Long> roots) throws Exception {
        Map<Long, Node> nodes = new HashMap<>();

        // Create nodes
        for (Long parent : tree.keySet()) {
            nodes.putIfAbsent(parent, node("ID " + parent));
            for (Long child : tree.get(parent)) {
                nodes.putIfAbsent(child, node("ID " + child));
            }
        }

        // Build graph
        Graph g = graph("dynasty").directed();
        for (Map.Entry<Long, List<Long>> entry : tree.entrySet()) {
            Node parentNode = nodes.get(entry.getKey());
            for (Long childId : entry.getValue()) {
                Node childNode = nodes.get(childId);
                g = g.with(parentNode.link(childNode));
            }
        }

        // Render as PNG
        File out = new File("dynasty-tree-" + System.currentTimeMillis() + ".png");
        Graphviz.fromGraph(g).render(Format.PNG).toFile(out);

        return out;
    }
}
