package spider;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.htmlparser.util.ParserException;

public class FileDownLoader {
	private OutputStream output = null;
	String fileName = null;
	public FileDownLoader(String name) throws FileNotFoundException {
		// TODO Auto-generated constructor stub
		fileName = name;
		output = new FileOutputStream(name);
	}
	public OutputStream GetStream(){
		return output;
	}
	public void writeIn(InputStream input) throws IOException, ParserException{
		int b = -1;
		while((b = input.read()) > 0){
			//往文件里写入
			output.write(b);
		}
		output.flush();
		System.out.println("执行了该活动");
	}
	public void close() throws IOException{
		output.close();
	}

}
