package me.suhsaechan.somansabusreservation.object.constants;

import lombok.Getter;

@Getter
public enum BusRoute {
  BUS_0655_DANGSAN_1("06:55 당산역 1호 - 출근", 46563, "출근"),
  BUS_0705_GUNJA_1("07:05 군자 1호 - 출근", 46569, "출근"),
  BUS_0710_DANGSAN_2("07:10 당산역 2호 - 출근", 46565, "출근"),
  BUS_0720_DANGSAN_3("07:20 당산역 3호 - 출근", 46567, "출근"),
  BUS_0720_SEOUL_1("07:20 서울역 1호 - 출근", 46552, "출근"),
  BUS_0735_GUNJA_2("07:35 군자 2호 - 출근", 46571, "출근"),
  BUS_0750_SEOUL_2("07:50 서울역 2호 - 출근", 46561, "출근"),
  BUS_1745_GUNJA_1("17:45 군자 1호 - 퇴근", 46570, "퇴근"),
  BUS_1745_SEOUL_1("17:45 서울역 1호 - 퇴근", 46553, "퇴근"),
  BUS_1745_DANGSAN_1("17:45 당산역 1호 - 퇴근", 46564, "퇴근"),
  BUS_1815_GUNJA_2("18:15 군자 2호 - 퇴근", 46572, "퇴근"),
  BUS_1815_DANGSAN_2("18:15 당산역 2호 - 퇴근", 46566, "퇴근"),
  BUS_1815_SEOUL_2("18:15 서울역 2호 - 퇴근", 46562, "퇴근"),
  BUS_1830_DANGSAN_3("18:30 당산역 3호 - 퇴근", 46568, "퇴근");

  private final String description;
  private final int disptid;
  private final String caralias;

  BusRoute(String description, int disptid, String caralias) {
    this.description = description;
    this.disptid = disptid;
    this.caralias = caralias;
  }
}
