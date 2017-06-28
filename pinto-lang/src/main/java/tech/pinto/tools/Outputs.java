package tech.pinto.tools;

import java.text.NumberFormat;
import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collector;
import java.util.stream.Stream;

import tech.pinto.ColumnValues;
import tech.pinto.time.PeriodicRange;

public class Outputs {

	public static Collector<ColumnValues,ArrayList<ColumnValues>,Optional<StringTable>> doubleDataToStringTable() {
		return doubleDataToStringTable(NumberFormat.getInstance());
	}
	
	public static Collector<ColumnValues,ArrayList<ColumnValues>,Optional<StringTable>> doubleDataToStringTable(NumberFormat nf) {
		return Collector.of(ArrayList::new, ArrayList::add,
				(left, right) -> { left.addAll(right); return left; }, 
				l -> {
					if(l.size() == 0) {
						return Optional.empty();
					}
					PeriodicRange<?> range = l.get(0).getRange();
					List<LocalDate> dates = range.dates();
					String[] labels = Stream.concat(Stream.of("Date"), l.stream().map(ColumnValues::getText)).toArray(i -> new String[i]);
					String[][] table = new String[(int) range.size()][l.size() + 1];
					for(int i = 0; i < range.size(); i++) {
						table[i][0] = dates.get(i).toString();
					}
					for (AtomicInteger i = new AtomicInteger(0); i.get() < l.size(); i.incrementAndGet()) {
						AtomicInteger j = new AtomicInteger(0);
						l.get(i.get()).getSeries().forEach(d -> table[j.getAndIncrement()][i.get() + 1] = nf.format(d));
					}	
					return Optional.of(new StringTable(labels,table));
				});
	}
	
	public static class StringTable {
		
		private final String[] header;
		private final String[][] cells;

		public StringTable(String[] header, String[][] cells) {
			this.header = header;
			this.cells = cells;
		}
		public String[] getHeader() { return header; }
		public String[][] getCells() { return cells; }
		
		
	}
}

