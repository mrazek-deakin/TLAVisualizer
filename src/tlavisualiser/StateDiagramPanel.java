package tlavisualiser;

import javax.swing.JPanel;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Stroke;

import javax.swing.JScrollPane;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.samples.SimpleGraphDraw;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

import stateextractor.StateExtractor;
import stateextractor.StateTransition;
import tlc2.tool.TLCState;
import javax.swing.JLabel;
import javax.swing.ScrollPaneConstants;
import javax.xml.transform.Transformer;

public class StateDiagramPanel extends JPanel {
	/**
	 * Create the panel.
	 */
	JScrollPane scrollPane;
	public StateDiagramPanel() {
		setLayout(new BorderLayout(0, 0));

//		scrollPane = new JScrollPane();
//		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
//		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
//		add(scrollPane, CENTER);
	}

	public void drawDiagram(StateExtractor se) {
		Graph<TLCState, StateTransition> g = new SparseMultigraph<TLCState, StateTransition>();
	


		for (TLCState temp : se.getAllStates()) {
			g.addVertex(temp);
		}

		for (StateTransition temp : se.getTransitions()) {
			g.addEdge(temp, temp.getInitialState(), temp.getFinalState(), EdgeType.DIRECTED);
		}

		Layout<TLCState, StateTransition> layout = new CircleLayout<TLCState, StateTransition>(g);
		layout.setSize(getSize());

		BasicVisualizationServer<TLCState, StateTransition> vv =
				new BasicVisualizationServer<TLCState, StateTransition>(layout);
		vv.setPreferredSize(getSize());

		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);










		add(vv);
		revalidate();
		repaint();


	}

}
