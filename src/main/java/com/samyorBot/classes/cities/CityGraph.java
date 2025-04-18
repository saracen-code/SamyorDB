package com.samyorBot.classes.cities;

import com.samyorBot.database.CityDAO;
import com.samyorBot.database.ConnectionDAO;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/** Internal edge representation */
class Edge {
    final City to;
    final double weight;
    final ConnectionType type;
    Edge(City to, double weight, ConnectionType type) {
        this.to = to;
        this.weight = weight;
        this.type = type;
    }
}

/**
 * Weighted, undirected graph of medieval cities with multiple route‑types.
 * Can be built in‑memory or loaded directly from SQL.
 */
public class CityGraph {
    private final Map<City, List<Edge>> adj = new HashMap<>();

    public CityGraph() {
        // empty: you can addCity/addConnection manually
    }

    /** Load entire graph from the DB. */
    public static CityGraph loadFromDatabase() throws SQLException {
        CityGraph graph = new CityGraph();

        // 1) load cities
        List<City> cities = CityDAO.getAllCities();
        for (City c : cities) {
            graph.adj.put(c, new ArrayList<>());
        }

        // 2) load connections
        for (ConnectionDAO.Conn rec : ConnectionDAO.getAllConnections()) {
            City from = graph.findById(cities, rec.fromId());
            City to   = graph.findById(cities, rec.toId());
            // enforce route‑type constraints
            if (rec.type() == ConnectionType.NAVAL
                    && (!from.isCoastal() || !to.isCoastal())) continue;
            if (rec.type() == ConnectionType.RIVERINE
                    && (!from.isRiverine() || !to.isRiverine())) continue;

            graph.adj.get(from).add(new Edge(to, rec.weight(), rec.type()));
            graph.adj.get(to).add(new Edge(from, rec.weight(), rec.type()));
        }

        return graph;
    }

    // Helper to match City by its DB id
    private City findById(List<City> list, int id) {
        return list.stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("City id not found: " + id));
    }

    /** Add a city node manually. */
    public void addCity(String name, boolean coastal, boolean riverine) {
        City city = new City(name, coastal, riverine);
        adj.putIfAbsent(city, new ArrayList<>());
    }

    /** Remove a city and all its edges. */
    public void removeCity(String name) {
        City c = findCity(name);
        adj.remove(c);
        adj.values().forEach(list -> list.removeIf(e -> e.to.equals(c)));
    }

    /** List all cities. */
    public List<City> listCities() {
        return new ArrayList<>(adj.keySet());
    }

    /**
     * Add an undirected connection of given type and weight.
     * NAVAL requires both coastal; RIVERINE requires both riverine.
     */
    public void addConnection(String from, String to, double weight, ConnectionType type) {
        City a = findCity(from), b = findCity(to);
        if (type == ConnectionType.NAVAL && (!a.isCoastal() || !b.isCoastal()))
            throw new IllegalArgumentException("NAVAL needs coastal endpoints");
        if (type == ConnectionType.RIVERINE && (!a.isRiverine() || !b.isRiverine()))
            throw new IllegalArgumentException("RIVERINE needs riverine endpoints");

        adj.get(a).add(new Edge(b, weight, type));
        adj.get(b).add(new Edge(a, weight, type));
    }

    /** Remove that exact connection (by type). */
    public void removeConnection(String from, String to, ConnectionType type) {
        City a = findCity(from), b = findCity(to);
        adj.get(a).removeIf(e -> e.to.equals(b) && e.type == type);
        adj.get(b).removeIf(e -> e.to.equals(a) && e.type == type);
    }

    /**
     * Find the nearest other city by total weight (Dijkstra).
     * Returns null if none reachable.
     */
    public City findClosestCity(String startName) {
        City start = findCity(startName);
        Map<City, Double> dist = new HashMap<>();
        for (City c : adj.keySet()) dist.put(c, Double.POSITIVE_INFINITY);
        dist.put(start, 0.0);

        PriorityQueue<City> pq = new PriorityQueue<>(Comparator.comparingDouble(dist::get));
        pq.add(start);

        while (!pq.isEmpty()) {
            City u = pq.poll();
            double d0 = dist.get(u);
            for (Edge e : adj.get(u)) {
                double nd = d0 + e.weight;
                if (nd < dist.get(e.to)) {
                    dist.put(e.to, nd);
                    pq.remove(e.to);
                    pq.add(e.to);
                }
            }
        }

        return dist.entrySet().stream()
                .filter(en -> !en.getKey().equals(start))
                .filter(en -> en.getValue() < Double.POSITIVE_INFINITY)
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    /** List all directly adjacent cities from the given city. */
    public List<City> adjacentCities(String name) {
        City c = findCity(name);
        return adj.get(c).stream()
                .map(e -> e.to)
                .collect(Collectors.toList());
    }

    /**
     * Get the connection type between two directly connected cities.
     * Throws if they are not directly connected.
     */
    public ConnectionType getConnectionType(String from, String to) {
        City a = findCity(from), b = findCity(to);
        for (Edge e : adj.get(a)) {
            if (e.to.equals(b)) {
                return e.type;
            }
        }
        throw new NoSuchElementException(
                "No direct connection from " + from + " to " + to
        );
    }

    /** Helper to locate a city object by name (throws if missing). */
    private City findCity(String name) {
        return adj.keySet().stream()
                .filter(c -> c.getName().equals(name))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("City not found: " + name));
    }
}
