import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import java.util.*;

class Start extends JPanel
{
	private double[] values;
	private String footer;

	public Start(double[] v, String foot)
	{
		values=v;
		footer=foot;
	}
	
	void drawArrowLine(Graphics g, int x1, int y1, int x2, int y2, int d, int h)
	{
		int dx=x2-x1, dy=y2-y1;
		double xm=Math.sqrt(dx*dx+dy*dy)-d;
		double xn=xm, ym=h, yn=-h, x;
		double sin=dy/Math.sqrt(dx*dx+dy*dy), cos=dx/Math.sqrt(dx*dx + dy*dy);

		x=xm*cos-ym*sin+x1;
		ym=xm*sin+ym*cos+y1;
		xm=x;
		x=xn*cos-yn*sin+x1;
		yn=xn*sin+yn*cos+y1;
		xn=x;

		int[] xpoints={x2, (int) xm, (int) xn};
		int[] ypoints={y2, (int) ym, (int) yn};

		g.drawLine(x1, y1, x2, y2);
		g.fillPolygon(xpoints, ypoints, 3);
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		if (values==null || values.length==0)
		{
			return;
		}
		
		double minValue=0;
		double maxValue=0;
		
		for (int i=0; i<values.length; i++)
		{
			if (minValue>values[i])
			{
				minValue=values[i];
			}
			
			if (maxValue<values[i])
			{
				maxValue=values[i];
			}
		}

		Dimension d=getSize();
		int clientWidth=d.width;
		int clientHeight=d.height;
		int barWidth=clientWidth/values.length;

		Font labelFont=new Font("SansSerif", Font.PLAIN, 15);
		FontMetrics labelFontMetrics=g.getFontMetrics(labelFont);

		int footerWidth=0;
		int y=0;
		int x=(clientWidth-footerWidth)/2;
		int top=10;
		int bottom=labelFontMetrics.getHeight();
		
		if (maxValue==minValue)
		{	
			drawArrowLine(g,6,clientHeight-16,6,1,8,6);
			drawArrowLine(g,6,clientHeight-16,clientWidth,clientHeight-16,8,6);
			return;
		}
		
		double scale=(clientHeight-top-bottom)/(maxValue-minValue);
		y=clientHeight-labelFontMetrics.getDescent();
		g.setFont(labelFont);

		for (int i=0; i<values.length; i++)
		{
			int valueX;
			
			if (i==0)
			{
				valueX=i*barWidth+6;
			}
			else if (i==values.length-1)
			{
				valueX=i*barWidth-6;	
			}
			else
			{
				valueX=i*barWidth+1;
			}
			int valueY=top;
			int height=(int)(values[i]*scale);
				
			if (values[i]>=0)
			{
				valueY+=(int)((maxValue-values[i])*scale);
			}
			else
			{
				valueY+=(int)(maxValue*scale);
				height=-height;
			}
			
			if (i==values.length-1)
			{
				g.setColor(Color.green);
				g.fillRect(valueX+7, valueY, barWidth-10, height);
				g.setColor(Color.black);
				g.drawRect(valueX+7, valueY, barWidth-10, height);
				x=i*barWidth+(barWidth)/2;
			}
			else if (i==0)
			{
				g.setColor(Color.green);
				g.fillRect(valueX, valueY, barWidth-7, height);
				g.setColor(Color.black);
				g.drawRect(valueX, valueY, barWidth-7, height);
				x=i*barWidth+(barWidth)/2;
			}
			else
			{
				g.setColor(Color.green);
				g.fillRect(valueX, valueY, barWidth-2, height);
				g.setColor(Color.black);
				g.drawRect(valueX, valueY, barWidth-2, height);
				x=i*barWidth+(barWidth)/2;
			}
		}
		
		g.drawString(footer, 10, y);
		drawArrowLine(g, 6, y-16, 6, 1, 8, 6);
		drawArrowLine(g, 6, y-16, clientWidth, y-16, 8, 6);

	}

	public static void main(String[] argv)
	{
		JFrame f=new JFrame();
		JFileChooser fc=new JFileChooser();
		JButton readButton=new JButton("OPEN");
		Dimension d=Toolkit.getDefaultToolkit().getScreenSize();
		f.setSize(d.width-100, d.height-100);

		double[] values=new double[2];
		values[0]=0;

		readButton.addActionListener(ev ->
		{
			int returnVal=fc.showOpenDialog(f);
			if (returnVal==JFileChooser.APPROVE_OPTION)
			{
				File file=fc.getSelectedFile();
				try
				{
					BufferedReader input=new BufferedReader(new InputStreamReader(new FileInputStream(file)));
					System.out.println("Wybrano plik "+fc.getSelectedFile().getAbsolutePath());
					Scanner scanner=new Scanner(new File(fc.getSelectedFile().getAbsolutePath()));
					Scanner scannerC=new Scanner(new File(fc.getSelectedFile().getAbsolutePath()));
					int i=0;
					int minValue=0;
					int maxValue=0;
					int licznik=0, k=0;
					
					System.out.println("*** Wczytuje dane z pliku ***");
					while(scannerC.hasNext())
					{
						if (scannerC.hasNextInt())
						{
							int value=scannerC.nextInt();

							if (licznik==0)
							{
								minValue=value;
								maxValue=value;
							}
								
							if (value>maxValue)
							{
								maxValue=value;
							}

							if (value<minValue)
							{
								minValue=value;
							} 
						
							licznik++;
						}
						else
						{
							scannerC.next();
						}

					}
					scannerC.close();
					System.out.println("*** Zakonczono wczytanie pliku ***");
					
					int elementy=maxValue-minValue+1;
					double[][] histogram=new double[2][elementy];
						
					int minJ=minValue;
					int maxJ=maxValue;
					
					
					for(int j=0; j<elementy; j++)
					{
						histogram[0][j]=minJ;
						histogram[1][j]=0;
						
						//System.out.println("j="+j+" wartosc "+histogram[0][j]+" liczba powtorzen "+histogram[1][j]);
						
						minJ++;
					}
				
					System.out.println("Liczba elementow = "+licznik);
					double[] valuesV=new double[elementy];
					System.out.println("*** Rozpoczeto przetwarzanie danych do histogramu ***");
					
					while (scanner.hasNext())
					{
						if (scanner.hasNextInt())
						{
							int value=scanner.nextInt();

							for(int j=0; j<elementy; j++)
							{
								if (value==histogram[0][j])
								{
									histogram[1][j]=histogram[1][j]+1;
								}
							}
						}
						else
						{
							scanner.next();
						}
						i++;
					}
					scanner.close();
					System.out.println("*** Zakonczono przetwarzanie danych do histogramu ***");
					
					System.out.println("Min przedzialu = "+minValue);
					System.out.println("Max przedzialu = "+maxValue);
					
					int klasy=0;
					
					for(int j=0; j<elementy; j++)
					{
						if (histogram[1][j]!=0)
						{
							klasy++;
						}
						valuesV[j]=histogram[1][j];	
					
						System.out.println("Liczba "+histogram[0][j]+" posiada "+histogram[1][j]+" powtorzen");
					}

					f.getContentPane().removeAll();
					f.getContentPane().revalidate();
					f.getContentPane().repaint();
					String info="Liczba klas: ";
					info+=klasy;
					info+=" Calkowita liczba zliczen: ";
					info+=licznik;
					f.getContentPane().add(new Start(valuesV,info));
					f.getContentPane().add(readButton, BorderLayout.PAGE_END);
					System.out.println("!!! Histogram zostal narysowany !!!");
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
			}
		});

		f.getContentPane().add(new Start(values, ""));
		f.getContentPane().add(readButton, BorderLayout.PAGE_END);

		WindowListener wndCloser=new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		};
		
		f.addWindowListener(wndCloser);
		f.setVisible(true);
	}
}
