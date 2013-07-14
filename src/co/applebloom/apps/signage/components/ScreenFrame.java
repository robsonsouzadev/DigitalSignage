package co.applebloom.apps.signage.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import co.applebloom.apps.signage.rendering.ChiHTMLEditorKit;
import co.applebloom.apps.signage.server.Server;
import co.applebloom.apps.signage.tag.HTMLElement;

import com.impetus.annovention.ClasspathDiscoverer;
import com.impetus.annovention.Discoverer;

public class ScreenFrame extends JFrame
{
	private static final long serialVersionUID = -8415026498095675707L;
	
	private JEditorPane edit = new JEditorPane();
	private JLabel loadingComponent = null;
	
	public ScreenFrame()
	{
		super( "Digital Signage App" );
		
		setLocationRelativeTo( null );
		setUndecorated( true );
		setScreen( 0 );
		
		setBackground( Color.WHITE );
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		loadingScreen( true );
	}
	
	public String getPage( String packSource ) throws IOException
	{
		// Get page from the built-in resin server which means PHP is executed and can access Java methods.
		return Server.getResinServer().request( "GET /packs/" + packSource + "/source.php" );
	}
	
	public void initDisplay( String packSource )
	{
		try
		{
			setLoadingText( "Creating Display" );
			
			getContentPane().add( new JScrollPane( edit ), BorderLayout.CENTER );
			edit.setEditable( false );
			edit.setEditorKit( new ChiHTMLEditorKit() );
			
			Thread.sleep( 200 );
			
			setLoadingText( "Rendering Source" );
			
			edit.setText( getPage( packSource ) );
			
			Thread.sleep( 200 );
			
			setLoadingText( "Finishing Up" );
			
			Thread.sleep( 200 );
			
			setLoadingText( "Done" );
			
			Thread.sleep( 200 );
			
			loadingScreen( false );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	private void createLoadingScreen()
	{
		ImageIcon ii = new ImageIcon( ScreenFrame.class.getClassLoader().getResource( "resources" ).toExternalForm().substring( 5 ) + "/wheelofdeath.gif" );
		loadingComponent = new JLabel( ii );
		loadingComponent.setFont( new Font( "Ubuntu", Font.BOLD, 36 ) );
		loadingComponent.setOpaque( true );
		loadingComponent.setBackground( Color.BLACK );
		loadingComponent.setForeground( Color.RED );
		setLoadingText( "" );
		getContentPane().add( loadingComponent, BorderLayout.CENTER );
	}
	
	public void loadingScreen( boolean visible )
	{
		if ( loadingComponent == null )
			createLoadingScreen();
		
		loadingComponent.setVisible( visible );
	}
	
	public void setLoadingText( String str )
	{
		if ( str.isEmpty() )
		{
			loadingComponent.setText( "Initalizing Digital Signage..." );
		}
		else
		{
			loadingComponent.setText( "Initalizing Digital Signage... " + str + "..." );
		}
	}
	
	public void setFullscreen()
	{
		setFullscreen( 0, true );
	}
	
	public void setFullscreen( int index )
	{
		setFullscreen( index, true );
	}
	
	public void setFullscreen( boolean yes )
	{
		setFullscreen( 0, yes );
	}
	
	public void setFullscreen( int index, boolean yes )
	{
		try
		{
			GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice[] devices = g.getScreenDevices();
			devices[index].setFullScreenWindow( yes ? this : null );
		}
		catch ( ArrayIndexOutOfBoundsException e )
		{
			System.err.println( "We tried to create a display on monitor #" + index + " which returned an ArrayIndexOutOfBounds, FAILED!" );
		}
	}
	
	public void setScreen( int index )
	{
		try
		{
			Rectangle r = getScreenBounds( index );
			
			setSize( r.width, r.height );
			setLocation( r.x, r.y );
		}
		catch ( ArrayIndexOutOfBoundsException e )
		{
			System.err.println( "We tried to create a display on monitor #" + index + " which returned an ArrayIndexOutOfBounds, FAILED!" );
		}
	}
	
	public static Rectangle getScreenBounds( int index ) throws ArrayIndexOutOfBoundsException
	{
		GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] devices = g.getScreenDevices();
		
		return devices[index].getDefaultConfiguration().getBounds();
	}
	
	public static int getNumberOfMonitors()
	{
		GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
		return g.getScreenDevices().length;
	}
}
