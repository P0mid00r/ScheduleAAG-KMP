package com.pomidorka.scheduleaag

object Strings {
//    private const val PROXY = ""
//    private const val PROXY = "https://thingproxy.freeboard.io/fetch/"
//    private const val PROXY = "https://cors-anywhere.com/"
//    private const val PROXY = "https://crossorigin.me/"
    const val PROXY = "https://corsproxy.io/"

    const val PROGRESS_DIALOG_SCHEDULE = "Загрузка расписания…"
    const val PROGRESS_DIALOG_LOADING_PAGE = "Загрузка страницы…"

    const val SERVER_CONNECTION_ERROR = "Сервер не доступен, попробуйте позже"
    const val SITE_CONNECTION_ERROR = "Ошибка подключения к сайту"
    const val NOT_FOUND_URL = "Ссылка не доступна, попробуйте еще раз"
    const val PDF_URL_NOT_FOUND = "Произошла ошибка при загрузке pdf, попробуйте попытку позже"
    const val SCHEDULE_TODAY_ERROR = "На сегодня нет расписания"
    const val SCHEDULE_NEXT_DAY_ERROR = "На следующий день нет расписания"

    const val HTML_INFO_DIALOG = """
        <body>
            <br>
            <b>Разработчик:</b>
            <p>Сергей Белоусов: <a href='https://t.me/Fomati147' target=\\\"_blank\\\">Fomati147</a></p>
            <br>
            <b>Дизайнер логотипа:</b>
            <p>Виктория Роганова: <a href='https://t.me/viktori_rar' target=\\\"_blank\\\">viktori_rar</a></p>
            <br>
            <p>Телеграмм канал: <a href='https://t.me/scheduleaag' target=\\\"_blank\\\">ScheduleAAG</a></p>
        </body>
    """

    const val HTML_CALLS = """
<div class="mtext">
    <style>.small{font-size:11px;}</style><h3>Учебный корпус №1, №2, №3</h3>
    <table id="table">
        <tbody>
            <tr>
                <th>Пара</th>
                <th>Время (Пн)</th>
                <th>Время (Вт-Пт)</th>
                <th>Время (Сб)</th>
            </tr>    
            <tr><th colspan="4">первая смена</th></tr>
            <tr style="color:red;"><td>классный час</td><td>8:00 - 8:45</td><td>&nbsp;</td><td>&nbsp;</td></tr>
            <tr class="small"><td>перерыв</td><td>5 минут</td><td>&nbsp;</td><td>&nbsp;</td></tr>
            <tr><td>1 пара</td><td>08:50 - 10:20</td><td>08:00 - 09:30</td><td>08:00 - 09:30</td></tr>
            <tr class="small"><td>перерыв</td><td>10 мин</td><td>10 мин</td><td>10 мин</td></tr>
            <tr><td>2 пара</td><td>10:30 - 12:00</td><td>09:40 - 11:10</td><td>09:40 - 11:10</td></tr>
            <tr class="small"><td>перерыв</td><td>20 мин</td><td>20 мин</td><td>20 мин</td></tr>
            <tr><td>3 пара</td><td>12:20 - 13:50</td><td>11:30 - 13:00</td><td>11:30 - 13:00</td></tr>
            <tr class="small"><td>перерыв</td><td>5 мин</td><td>10 мин</td><td>5 мин</td></tr>
            <tr><th colspan="4">вторая смена</th></tr>
            <tr style="color:red;"><td>классный час</td><td>13:55 - 14:40</td><td>&nbsp;</td><td>&nbsp;</td></tr>
            <tr class="small"><td>перерыв</td><td>10 минут</td><td>&nbsp;</td><td>&nbsp;</td></tr>
            <tr><td>4 пара</td><td>14:50 - 16:20</td><td>13:10 - 14:40</td><td>13:05 - 14:35</td></tr>
            <tr class="small"><td>перерыв</td><td>20 мин</td><td>10 мин</td><td>20 мин</td></tr>
            <tr><td>5 пара</td><td>16:40 - 18:10</td><td>14:50 - 16:20</td><td>14:55 - 16:25</td></tr>
            <tr class="small"><td>перерыв</td><td>5 мин</td><td>20 мин</td><td>5 мин</td></tr>
            <tr><td>6 пара</td><td>18:15 - 19:45</td><td>16:40 - 18:10</td><td>16:30 - 18:00</td></tr>
            <tr class="small"><td>перерыв</td><td>&nbsp;</td><td>5 мин</td><td>&nbsp;</td></tr>
            <tr><td>7 пара</td><td>&nbsp;</td><td>18:15 - 19:45</td><td>&nbsp;</td></tr>
        </tbody>    
    </table>    
</div>
"""

    const val TABLE_CSS = """
#table {
	color:#333;
	font-size:14px;
	text-shadow: 1px 1px 0px #fff;
	background:#eaebec;
	margin:20px 0;
	border:1px #ccc solid;
  border-spacing:0px;
	border-collapse:separate;

	-moz-border-radius:3px;
	-webkit-border-radius:3px;
	border-radius:3px;

	-moz-box-shadow: 0 1px 2px #d1d1d1;
	-webkit-box-shadow: 0 1px 2px #d1d1d1;
	box-shadow: 0 1px 2px #d1d1d1;
  width:100%;
}

#table th {
	font-weight:bold;
	padding:11px 15px 12px 15px;
	border-top:1px solid #fafafa;
	border-bottom:1px solid #e0e0e0;

	background: #ededed;
	background: -webkit-gradient(linear, left top, left bottom, from(#ededed), to(#ebebeb));
	background: -moz-linear-gradient(top,  #ededed,  #ebebeb);
}
#table th:first-child{
	padding-left:20px;
}
#table tr:first-child th:first-child{
	-moz-border-radius-topleft:3px;
	-webkit-border-top-left-radius:3px;
	border-top-left-radius:3px;
}
#table tr:first-child th:last-child{
	-moz-border-radius-topright:3px;
	-webkit-border-top-right-radius:3px;
	border-top-right-radius:3px;
}
#table tr{
	text-align: center;
	padding-left:20px;
}
#table tr td:first-child{
	padding-left:20px;
	border-left: 0;
}
#table tr td {
	padding:8px;
	border-top: 1px solid #ffffff;
	border-bottom:1px solid #e0e0e0;
	border-left: 1px solid #e0e0e0;

	background: #fafafa;
	background: -webkit-gradient(linear, left top, left bottom, from(#fbfbfb), to(#fafafa));
	background: -moz-linear-gradient(top,  #fbfbfb,  #fafafa);
}
#table tr:nth-child(even) td{
	background: #f6f6f6;
	background: -webkit-gradient(linear, left top, left bottom, from(#f8f8f8), to(#f6f6f6));
	background: -moz-linear-gradient(top,  #f8f8f8,  #f6f6f6);
}
#table tr:last-child td{
	border-bottom:0;
}
#table tr:last-child td:first-child{
	-moz-border-radius-bottomleft:3px;
	-webkit-border-bottom-left-radius:3px;
	border-bottom-left-radius:3px;
}
#table tr:last-child td:last-child{
	-moz-border-radius-bottomright:3px;
	-webkit-border-bottom-right-radius:3px;
	border-bottom-right-radius:3px;
}
#table tr:hover td{
	background: #f2f2f2;
	background: -webkit-gradient(linear, left top, left bottom, from(#f2f2f2), to(#f0f0f0));
	background: -moz-linear-gradient(top,  #f2f2f2,  #f0f0f0);
}

#table tr td a:link {
	color: #666;
	font-weight: bold;
	text-decoration:none;
}
#table tr td a:visited {
	color: #666;
	font-weight:bold;
	text-decoration:none;
}
#table tr td a:active,
#table tr td a:hover {
	color: #bd5a35;
	text-decoration:underline;
}
"""
}