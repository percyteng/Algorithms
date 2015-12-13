package cisc365lab2;
/*Percy Teng 10122592 13spt1*/
//this class creates objects of items
public class item {
	int value;
	int mass;
	double ratio;
	int index;
	public item(int value, int mass, int index){
		this.value = value;
		this.mass = mass;
		this.ratio = (double)value/mass;
		this.index = index;
	}
}
