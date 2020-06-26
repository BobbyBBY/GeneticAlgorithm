package aicourse;
import java.util.Arrays;
/**
 * 
* @ClassName: Heredity 
* @Description: TODO遗传算法
* @author xuanpengyu@faxmail.com
* @date 2019年5月14日 下午5:02:05 
*
 */
public class Heredity {
	/*
	 public static void heredity() {
		
				Heredity heredity = new Heredity(30,10000,0.7,0.1,
												1000,0.0001,10,
												new Comparator() {
					@Override
					public double adaptabilityFun(DNA dna,int maxValueX, int maxValueY, int minValueY) {
						double x = getY(dna,maxValueX,maxValueY,minValueY);
						return 45*x-(x*x);
					}

					@Override
					public int calMaxValueX(int length) {//自变量最大值
						int maxDecimal = 1;
						for(int i=0;i<length;++i) {//计算十进制最大值
							maxDecimal*=2;
						}
						return maxDecimal;
					}

					@Override
					public int calMaxValueY(int length) {//因变量最大值
						// TODO 自动生成的方法存根
						return 100;
					}

					@Override
					public int calMinValueY(int length) {//因变量最小值
						// TODO 自动生成的方法存根
						return 0;
					}

					@Override
					public double getY(DNA dna,int maxValueX, int maxValueY, int minValueY) {//获得因变量
						// TODO 自动生成的方法存根
						int value = 0;
						for(int i=dna.dna.length-1;i>=0;--i) {
							value<<=1;
							value+=dna.dna[i];
						}
						return (double)(maxValueY-minValueY)*((double)value/maxValueX);
					}
				});
				
				DNA result = heredity.start();
				System.out.println(result.adaptability);
				System.out.println(heredity.getValue(result));
	}
	 * */

	public interface Comparator{

		double adaptabilityFun(DNA dna, int maxValueX, int maxValueY, int minValueY);//比较器，即适应性函数
		double getY(DNA dna,int maxValueX, int maxValueY, int minValueY);
		int calMaxValueX(int length);//分布函数自变量上限
		int calMaxValueY(int length);//分布函数因变量上限
		int calMinValueY(int length);//分布函数因变量下限
	}
	
	public class DNA{
		int[] dna;//基因
		double adaptability;//适应度
		DNA(int DNALength){
			dna = new int[DNALength];
			adaptability = 0d;
		}

	}
	
	protected int DNALength;//染色体长度
	protected int DNACounts;//种群数量，染色体数量
	protected double Pc;//交叉概率。0%~100%
	protected double Pm;//变异概率。0%~100%
	protected int Counts;//迭代次数上限
	protected double Threshold;//适应性阀值。0%~100%
	protected long Time;//程序运行时间上限，单位为ms
	protected DNA[] DNAs;//种群
	protected Comparator comparator;//比较器
	protected int maxValueX;//分布函数自变量上限
	protected int maxValueY;//分布函数因变量上限
	protected int minValueY;//分布函数因变量下限
	protected double lastMaxAdaptability;//上一次最大适应值，用于判断是否达到适应性阀值
	protected boolean notFirst;//是否是第一次计算适应值,false:是第一次，true：不是第一次
	
	
	
	Heredity(int DNALength, int DNACounts, double Pc, double Pm, int Counts, double Threshold, long Time, Comparator comparator){//参数Time单位为秒
		this.DNALength = DNALength;
		this.DNACounts = DNACounts;
		this.Pc = Pc;
		this.Pm = Pm;
		this.Counts = Counts;
		this.Threshold = Threshold;
		this.Time = Time*1000;//时间单位转换
		this.comparator = comparator;
		maxValueX = comparator.calMaxValueX(DNALength);
		maxValueY = comparator.calMaxValueY(DNALength);
		minValueY = comparator.calMinValueY(DNALength);
		DNAs = new DNA[DNACounts];
		for(int i=0;i<DNACounts;++i) {
			DNAs[i] = new DNA(DNALength);
		}
		notFirst=false;
	}
	
	public DNA start() {
		long startTime = System.currentTimeMillis();
		DNA temp;
		randomDNAs();
		while(true) {
			--Counts;
			if(calculateAdaptability()) {//适应度达到阀值
				break;
			}
			if(Counts<=0) {//迭代次数达上限
				break;
			}
			if(System.currentTimeMillis()-startTime>=Time) {//运行时间达上限
				break;
			}
			cross();//交叉
			variation();//变异
		}
		temp = DNAs[0];
		for(DNA dna:DNAs) {
			if(dna.adaptability>temp.adaptability) {
				temp = dna;
			}
		}
		return temp;
	}
	
	protected void cross() {//交叉，轮盘赌法
		DNA[] DNAsNew = new DNA[DNACounts];//新的种群
		double[] accumulate = new double[DNACounts];//累计适应度比率
		double sum = 0d;//总计适应度值
		double sumP = 0d;//累计适应度值
		for(DNA dna:DNAs) {//计算总计适应度值
			sum+=dna.adaptability;
		}
		for(int i=0;i<DNACounts;++i) {//计算累计适应度比率
			sumP += DNAs[i].adaptability/sum;
			accumulate[i] =  sumP;
		}
		int pointer;
		for(int i=0;i<DNACounts;++i) {//轮盘赌法
			pointer = Arrays.binarySearch(accumulate, Math.random());//二分查找，找到轮盘指针位置
			if(pointer < 0) {
				pointer = -pointer - 1;
			}
			if(pointer >= DNACounts) {
				pointer = DNACounts - 1;
			}
			DNAsNew[i] = copyOf(DNAs[pointer]);
		}
		for(int i=0;i<DNACounts;i+=2) {//每两个dna交叉
			if(i==DNACounts-1) {//奇数个种群的最后一个基因不交叉
				break;
			}
			if(Math.random()<=Pc) {
				crossDna(DNAsNew[i],DNAsNew[i+1]);
			}
		}
		DNAs = DNAsNew;
	}
	
	
	protected void variation() {//变异
		for(int i=0;i<DNACounts;++i) {
			for(int j = 0;j<DNALength;++j) {
				if(Math.random()<=Pm) {
					DNAs[i].dna[j] = (int)(Math.random()*2);
				}
			}
		}
	}
	
	/*
	 * 返回值false:当前没有达到适应性阀值
	 * 返回值true:当前种群达到适应性阀值
	 * */
 	protected boolean calculateAdaptability() {//适应度计算
		for(DNA dna:DNAs) {
			dna.adaptability = comparator.adaptabilityFun(dna,maxValueX,maxValueY,minValueY);
		}
		//排序后,使用二分能够减少轮盘赌法中查找指针位置的时间
		Arrays.sort(DNAs, new java.util.Comparator<DNA>() {
			public int compare(DNA a, DNA b) {
				//降序
				if(a.adaptability<b.adaptability) {
					return 1;
				}
				else if(a.adaptability>b.adaptability) {
					return -1;
				}
				else {
					return 0;
				}
			}
		});
		if(notFirst&&(Math.abs(DNAs[0].adaptability-lastMaxAdaptability)/lastMaxAdaptability)<Threshold) {
			return true;
		}
		else {
			notFirst=true;
			lastMaxAdaptability=DNAs[0].adaptability;
			return false;
		}
	}
	
	protected void randomDNAs() {//初始化时，获取随机的dna种群
		for(int i=0;i<DNACounts;++i) {
			intToBinaryArray((int)(Math.random()*maxValueX), DNAs[i].dna);
		}
	}
	
	protected void crossDna(DNA dna1, DNA dna2) {//对两个dna进行随机位置的交叉
		for(int i=(int)(Math.random()*DNALength);i<DNALength;++i) {
			dna2.dna[i] += dna1.dna[i];
			dna1.dna[i] = dna2.dna[i] - dna1.dna[i];
			dna2.dna[i] -= dna1.dna[i];
		}
	}
	
	public DNA copyOf(DNA dna) {
		DNA result = new DNA(dna.dna.length);
		result.adaptability = dna.adaptability;
		result.dna = Arrays.copyOf(dna.dna,dna.dna.length);
		return result;
	}
	
	public void intToBinaryArray(int num, int[] array) {
		for(int i=0;i<DNALength;++i) {
			array[i] = num % 2;
			num /= 2;
		}
	}
	
	public double getValue(DNA dna) {
		return comparator.getY(dna,maxValueX,maxValueY,minValueY);
	}
	
}
