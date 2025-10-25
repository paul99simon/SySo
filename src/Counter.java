
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Counter {

	public static void main(String[] args) throws IOException {
		assert (args.length == 2);
		for(int i = 1; i <= Runtime.getRuntime().availableProcessors(); i++)
		{
			long start = System.currentTimeMillis();
			long result = countLinesInAllFiles(args[0], args[1], i);
			long end = System.currentTimeMillis();
			System.out.println(i + " threads found " + result + " lines in " + (end - start) + " ms");
		}
	}

	public static long countLines(String fileName) throws IOException {
		try (Stream<String> lines = Files.lines(Paths.get(fileName))) {
			return lines.count();
		}
	}

	public static long countLinesInAllFiles(String folderPath, String regex, int thread_number) throws IOException {
		
		Pattern pattern = Pattern.compile(regex);
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(thread_number);
		long sum = 0;

		try (Stream<Path> paths = Files.walk(Paths.get(folderPath))) {
			List<Future<Long>> results = paths
				.filter(Files::isRegularFile)
				.filter(path -> pattern.matcher(path.toString()).matches())
				.map(path -> executor.submit(() -> countLines(path.toString())))
				.toList(); 
			
			for (Future<Long> result : results)
			{
				try
				{
					sum += result.get();
				}
				catch (Exception ignored){}
			}
		}
		executor.shutdown();
		return sum;
	}
}
