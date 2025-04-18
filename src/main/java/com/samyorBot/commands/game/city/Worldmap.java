package com.samyorBot.commands.game.city;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxCellRenderer;

import com.samyorBot.classes.cities.City;
import com.samyorBot.classes.cities.ConnectionType;
import com.samyorBot.database.CityDAO;
import com.samyorBot.database.ConnectionDAO;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.utils.FileUpload;

import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;


public class Worldmap extends SlashCommand.Subcommand {

    public Worldmap() {
        setCommandData(new SubcommandData("worldmap", "Show the city map as an image"));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue(hook -> {
            try {
                // 1) Build the graph
                Graph<City, DefaultWeightedEdge> jgtGraph = buildYourGraph();

                // 2) Wrap in a JGraphXAdapter
                JGraphXAdapter<City, DefaultWeightedEdge> graphAdapter =
                        new JGraphXAdapter<>(jgtGraph);

                // 3) Layout in a circle
                new mxCircleLayout(graphAdapter).execute(graphAdapter.getDefaultParent());

                // 4) Render to PNG
                byte[] png = renderGraphToPng(graphAdapter);

                // 5) Send as a file upload
                hook.sendMessage("Here’s the current world map:")
                        .addFiles(FileUpload.fromData(png, "world-map.png"))
                        .queue();

            } catch (Exception e) {
                hook.sendMessage("❌ Failed to render map: " + e.getMessage())
                        .setEphemeral(true)
                        .queue();
            }
        });
    }

    /**
     * Build and return a JGraphT graph of City → City.
     */
    private Graph<City, DefaultWeightedEdge> buildYourGraph() throws SQLException {
        // 1) Fetch all cities and map by ID
        List<City> cities = CityDAO.getAllCities();
        Map<Integer,City> byId = new HashMap<>();
        for (City c : cities) byId.put(c.getId(), c);

        // 2) Create the graph
        SimpleWeightedGraph<City, DefaultWeightedEdge> g =
                new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

        // add all vertices
        cities.forEach(g::addVertex);

        // 3) Fetch all connections and add edges
        for (ConnectionDAO.Conn conn : ConnectionDAO.getAllConnections()) {
            City a = byId.get(conn.fromId());
            City b = byId.get(conn.toId());
            if (a == null || b == null) continue;  // skip bad data

            // enforce NAVAL/RIVERINE constraints if you like:
            if (conn.type() == ConnectionType.NAVAL && (!a.isCoastal() || !b.isCoastal())) continue;
            if (conn.type() == ConnectionType.RIVERINE && (!a.isRiverine()|| !b.isRiverine())) continue;

            DefaultWeightedEdge e = g.addEdge(a, b);
            if (e != null) {
                g.setEdgeWeight(e, conn.weight());
                // optionally store `conn.type()` in a separate map
            }
        }

        return g;
    }


    /** Helper to add an edge with a weight */
    private static void addEdge(Graph<City, DefaultWeightedEdge> g,
                                City a, City b, double weight) {
        DefaultWeightedEdge e = g.addEdge(a, b);
        if (e != null) {
            g.setEdgeWeight(e, weight);
        }
    }

    public static byte[] renderGraphToPng(JGraphXAdapter<City,DefaultWeightedEdge> graphAdapter)
            throws IOException {
        // 1) Gather all vertex‐ and edge‐cells
        Collection<mxICell> vertexCells = graphAdapter.getVertexToCellMap().values();
        Collection<mxICell> edgeCells = graphAdapter.getEdgeToCellMap().values();

        // 2) Copy into a List<Object> so we can pass to mxCellRenderer
        List<Object> cells = new ArrayList<>(vertexCells.size() + edgeCells.size());
        cells.addAll(vertexCells);   // Note: addAll(Collection<? extends Object>) is allowed
        cells.addAll(edgeCells);

        if (cells.isEmpty()) {
            throw new IOException("No cells to render – graph is empty");
        }

        // 3) Render exactly those cells
        BufferedImage image = mxCellRenderer.createBufferedImage(
                graphAdapter,
                cells.toArray(),   // now an Object[]
                1,                 // scale = 1×
                Color.WHITE,       // background
                true,              // anti‑alias
                null               // default renderer
        );
        if (image == null) {
            throw new IOException("mxCellRenderer.createBufferedImage returned null");
        }

        // 4) Write out the PNG
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ImageIO.write(image, "PNG", os);
            return os.toByteArray();
        }
    }


}
