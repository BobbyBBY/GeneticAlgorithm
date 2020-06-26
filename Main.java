package aicourse;

import java.io.IOException;

import aicourse.Heredity.Comparator;
import aicourse.Heredity.DNA;

/**
 * 这是当初的人工智能课作业，为了通用性，写得有点复杂
 */
public class Main {
	public static void main(String[] args) throws IOException {
		heredity();
	}
	public static void heredity() {
		/*dna长度,种群数量,交叉概率,变异概率     ,
		迭代次数上限,适应性阀值,程序运行时间上限(s),
		适应性比较器
		*/
				Heredity heredity = new Heredity(30,10000,0.7,0.1,
												1000,0.0001,10,
												new Comparator() {
					@Override
					public double adaptabilityFun(DNA dna,int maxValueX, int maxValueY, int minValueY) {
						double x = getY(dna,maxValueX,maxValueY,minValueY);
						return 45*x-(x*x);
					}

					@Override
					public int calMaxValueX(int length) {//分布函数自变量最大值
						int maxDecimal = 1;
						for(int i=0;i<length;++i) {//计算十进制最大值
							maxDecimal*=2;
						}
						return maxDecimal;
					}

					@Override
					public int calMaxValueY(int length) {//分布函数因变量最大值
						// TODO 自动生成的方法存根
						return 100;
					}

					@Override
					public int calMinValueY(int length) {//分布函数因变量最小值
						// TODO 自动生成的方法存根
						return 0;
					}

					@Override
					public double getY(DNA dna,int maxValueX, int maxValueY, int minValueY) {//获得分布函数因变量
						// TODO 自动生成的方法存根
						int value = 0;
						for(int i=dna.dna.length-1;i>=0;--i) {
							value<<=1;
							value+=dna.dna[i];
						}
						return (double)(maxValueY-minValueY)*((double)value/maxValueX);
					}
				});
				String theResult = "22.5";
				
				DNA result = heredity.start();
				System.out.println("算法得出最优解为：" + heredity.getValue(result));
				System.out.println("实际最优解为：" + theResult);
				
	}
}
