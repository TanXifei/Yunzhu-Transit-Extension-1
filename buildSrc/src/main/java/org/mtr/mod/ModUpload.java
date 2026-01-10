package org.mtr.mod;

import com.jonafanho.apitools.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ModUpload {

	private static final String[] MINECRAFT_VERSIONS = {"1.16.5", "1.17.1", "1.18.2", "1.19.2", "1.19.4", "1.20.1", "1.20.4"};


	public static void main(String[] args) throws IOException {
		if (args.length == 2) {
			for (final String minecraftVersion : MINECRAFT_VERSIONS) {
				for (final ModLoader modLoader : ModLoader.values()) {
					final String modVersion = String.format("%s-%s+%s", modLoader.name, args[0], minecraftVersion);
					final String modVersionUpperCase = String.format("%s-%s+%s", modLoader.name.toUpperCase(Locale.ENGLISH), args[0], minecraftVersion);
					final String fileName = String.format("Yunzhu-Transit-Extension-%s.jar", modVersion);
					final Path filePath = Paths.get("build/release").resolve(fileName);

					final Map<String, DependencyType> dependenciesModrinth = new HashMap<String, DependencyType>();
					if (modLoader == ModLoader.FABRIC) {
						dependenciesModrinth.put("P7dR8mSH", DependencyType.REQUIRED);
					}
					dependenciesModrinth.put("XKPAmI6u", DependencyType.REQUIRED);

					do {
					} while (!new ModId("nqMdKn6A", ModProvider.MODRINTH).uploadFile(
							modVersionUpperCase,
							modVersionUpperCase,
							"## \uD83C\uDF89元旦快乐，新年新气象！\n" +
									"**本mod依赖Minecraft Transit Railway 4.0.0 - 4.0.2 hotfix1**\n" +
									"### 新增方块\n" +
									"**Dewhurst 系列**\n" +
									"*   Dewhurst US81呼梯面板（类型1）\n" +
									"\n" +
									"**Dover 系列**\n" +
									"*   Dover Impulse呼梯面板（类型1）\n" +
									"*   Dover Impulse到站灯（类型1，横向，占一格/两格）\n" +
									"\n" +
									"**富士达 (Fujitec) 系列**\n" +
									"*   富士达MIC-400呼梯面板（类型1）\n" +
									"*   富士达MIC-400呼梯面板（类型1，旧款）\n" +
									"*   富士达MIC-400呼梯面板（类型1，无屏幕）\n" +
									"*   富士达MIC-400呼梯面板（类型1，旧款，无屏幕）\n" +
									"*   富士达MIC-400候梯楼层显示器（类型1，横向/纵向，占一格/两格）\n" +
									"\n" +
									"**日立 (Hitachi) 系列**\n" +
									"*   日立呼梯面板（通用）\n" +
									"*   日立B85候梯楼层显示器（类型1，横向/纵向，占一格/两格）\n" +
									"*   日立B89呼梯面板（类型1/2，带/无屏幕）\n" +
									"*   日立B89候梯楼层显示器（类型1，横向/纵向，占一格/两格）\n" +
									"*   日立VIB-320 & 320W呼梯面板 (HIP-43款式)\n" +
									"*   日立VIB-221 & 221W呼梯面板 (HIP-43款式)\n" +
									"*   日立GHL-668到站灯（占一格/两格）\n" +
									"*   日立GHL-673到站灯（占一格/两格）\n" +
									"*   日立VIB-628 & 628W呼梯面板\n" +
									"*   日立HB-628呼梯面板\n" +
									"*   日立HB-658呼梯面板\n" +
									"*   日立VIB-681 & 681W呼梯面板\n" +
									"*   日立VIB-676 & 676W呼梯面板\n" +
									"*   日立VIB-673 & 673W呼梯面板 (及 HIP-43款式)\n" +
									"*   日立HB-673呼梯面板\n" +
									"*   日立VIB-679 & 679W呼梯面板\n" +
									"*   日立VIB-181A & 181WA呼梯面板\n" +
									"*   日立VIB-182A & 182WA呼梯面板\n" +
									"*   日立HB-181A呼梯面板\n" +
									"*   日立NCA-MRL呼梯面板\n" +
									"*   日立VIB-616呼梯面板\n" +
									"\n" +
									"**通力 (KONE) 系列**\n" +
									"*   通力M系列候梯楼层显示器（类型1，小型，占一格/两格）\n" +
									"\n" +
									"**三菱 (Mitsubishi) 系列**\n" +
									"*   三菱MP-VF候梯楼层显示器（类型1，横向/纵向，占一格/两格）\n" +
									"*   三菱MP-VF到站灯（类型1，横向/纵向，占一格/两格）\n" +
									"*   三菱MP呼梯面板（类型1）\n" +
									"*   三菱MP触摸呼梯面板（类型1）\n" +
									"*   三菱PIM-A110呼梯面板\n" +
									"*   三菱HBM-A110呼梯面板\n" +
									"*   三菱PIN-A11呼梯面板\n" +
									"*   三菱HBN-A11呼梯面板\n" +
									"*   三菱PIH-D330候梯楼层显示器（占一格/两格）\n" +
									"*   三菱菱电升降机外显（类型2，占一格/两格）\n" +
									"*   三菱PIE-C210 & C220呼梯面板\n" +
									"*   三菱HBE-C210 & C220呼梯面板\n" +
									"*   三菱PI*-A634 & A644呼梯面板 (及 LCD款式、无屏幕款式)\n" +
									"*   三菱PI*-C634 & C644呼梯面板 (及 LCD款式、无屏幕款式)\n" +
									"*   三菱HLH-A31S到站灯（横向/纵向，占一格/两格）\n" +
									"*   三菱HLV-A31S到站灯（横向/纵向，占一格/两格）\n" +
									"*   三菱HLV-A21S到站灯（占一格/两格）\n" +
									"*   三菱PIH-C110候梯楼层显示器（占一格/两格）\n" +
									"*   三菱PIH-C130候梯楼层显示器（占一格/两格）\n" +
									"\n" +
									"**上海三菱 系列**\n" +
									"*   上海三菱ZPI*-GD10 & GD20呼梯面板\n" +
									"*   上海三菱ZHB*-G010呼梯面板\n" +
									"*   上海三菱ZPI*-CD12 & CD22呼梯面板\n" +
									"*   上海三菱ZPI*-GA13 & GA23呼梯面板\n" +
									"*   上海三菱ZPIH-C301候梯楼层显示器（占一格/两格）\n" +
									"*   上海三菱ZPIH-CE01候梯楼层显示器（占一格/两格）\n" +
									"\n" +
									"**奥的斯 (Otis) 系列**\n" +
									"*   奥的斯Series 1到站灯（类型1，占一格/两格）\n" +
									"*   奥的斯Series 1到站灯（类型1，带屏幕，占一格/两格）\n" +
									"\n" +
									"**迅达 (Schindler) 系列**\n" +
									"*   迅达S系列深灰色按钮呼梯面板\n" +
									"*   迅达S系列蓝色按钮呼梯面板\n" +
									"*   迅达R系列圆形按钮呼梯面板\n" +
									"*   迅达R系列候梯楼层显示器（类型1，占一格/两格）\n" +
									"*   迅达FI GS 700呼梯面板（无屏幕）\n" +
									"*   迅达FI GS 700触摸呼梯面板（无屏幕）\n" +
									"*   迅达FI GS 700候梯楼层显示器（钢制/黑色/灰色，占一格/两格）\n" +
									"\n" +
									"**东芝 (Toshiba) 系列**\n" +
									"*   东芝呼梯面板（类型1）\n" +
									"*   东芝到站灯（类型1，占一格/两格）\n" +
									"*   东芝候梯楼层显示器（类型1/2，占一格/两格）\n" +
									"\n" +
									"**铁路设施 (PAT) 系列**\n" +
									"*   PAT RS01指示牌\n" +
									"*   PAT RS01指示牌柱\n" +
									"\n" +
									"### 新增功能\n" +
									"*   已为部分到站灯添加音效\n" +
									"*   为部分三菱部件添加箭头旋转动画\n" +
									"\n" +
									"### 新增功能翻译\n" +
									"*   YTE工具类物品的英文名已根据新版本统一调整，中文名保持不变。\n" +
									"*   `message.floor_auto_setter_status_need_track_floor_position` 的翻译更新为：“请选中一个楼层轨道”。\n" +
									"\n" +
									"### 新增/更新工具提示（Tooltip）\n" +
									"**日立系列**\n" +
									"*   显示器型号：HIP-31-2\n" +
									"*   显示器型号：SCLC-LCD4\n" +
									"*   按钮型号：ML-MW\n" +
									"*   按钮型号：UL-MW\n" +
									"*   长沙国金中心款式\n" +
									"\n" +
									"**上海三菱系列**\n" +
									"*   显示器型号：ZLHH-110AG01\n" +
									"*   显示器型号：ZLHH-110DG01\n" +
									"*   显示器型号：ZLHH-101AG02\n" +
									"*   按钮型号：A11\n" +
									"*   按钮型号：A12\n" +
									"\n" +
									"### 优化内容\n" +
									"*   优化自定义字体缓存，当电梯楼层切换时，显示屏不再闪烁\n" +
									"\n" +
									"### 修复内容\n" +
									"*   修复横向轨道下批量楼层设置器失效的问题\n" +
									"\n" +
									"## ✨Happy New Year! YTE Mod Translation Update Log\n" +
									"\n" +
									"**English translations for some new blocks are currently missing in the translation files.**  \n" +
									"\n" +
									"**You need to install the official release of Minecraft Transit Railway 4.0.0-4.0.2 hotfix1 before installing this mod!**\n" +
									"\n" +
									"### New Block Translations\n" +
									"**Dewhurst Series**\n" +
									"*   Dewhurst US81 Call Panel(Type 1)\n" +
									"\n" +
									"**Dover Series**\n" +
									"*   Dover Impulse Button(Type 1)\n" +
									"*   Dover Impulse Lantern(Type 1, Horizontal, Odd/Even)\n" +
									"\n" +
									"**Fujitec Series**\n" +
									"*   Fujitec MIC-400 Call Panel(Type 1)\n" +
									"*   Fujitec MIC-400 Call Panel(Type 1, Old)\n" +
									"*   Fujitec MIC-400 Call Panel(Type 1, Without Screen)\n" +
									"*   Fujitec MIC-400 Call Panel(Type 1, Old, Without Screen)\n" +
									"*   Fujitec MIC-400 Screen(Type 1, Horizontal/Vertical, Odd/Even)\n" +
									"\n" +
									"**Hitachi Series**\n" +
									"*   Hitachi Call Panel\n" +
									"*   Hitachi B85 Screen(Type 1, Horizontal/Vertical, Odd/Even)\n" +
									"*   Hitachi B89 Call Panel(Type 1/2, With/Without Screen)\n" +
									"*   Hitachi B89 Screen(Type 1, Horizontal/Vertical, Odd/Even)\n" +
									"*   Hitachi VIB-320 & 320W Call Panel (HIP-43 version)\n" +
									"*   Hitachi VIB-221 & 221W Call Panel (HIP-43 version)\n" +
									"*   Hitachi GHL-668 Lantern (Odd/Even)\n" +
									"*   Hitachi GHL-673 Lantern (Odd/Even)\n" +
									"*   Hitachi VIB-628 & 628W Call Panel\n" +
									"*   Hitachi HB-628 Call Panel\n" +
									"*   Hitachi HB-658 Call Panel\n" +
									"*   Hitachi VIB-681 & 681W Call Panel\n" +
									"*   Hitachi VIB-676 & 676W Call Panel\n" +
									"*   Hitachi VIB-673 & 673W Call Panel (and HIP-43 version)\n" +
									"*   Hitachi HB-673 Call Panel\n" +
									"*   Hitachi VIB-679 & 679W Call Panel\n" +
									"*   Hitachi VIB-181A & 181WA Call Panel\n" +
									"*   Hitachi VIB-182A & 182WA Call Panel\n" +
									"*   Hitachi HB-181A Call Panel\n" +
									"*   Hitachi NCA-MRL Call Panel\n" +
									"*   Hitachi VIB-616 Call Panel\n" +
									"\n" +
									"**KONE Series**\n" +
									"*   Kone M-Series Hall Floor Indicator(Type 1, Small, Odd/Even)\n" +
									"\n" +
									"**Mitsubishi Series**\n" +
									"*   Mitsubishi MP-VF Screen (Type 1, Horizontal/Vertical, Odd/Even)\n" +
									"*   Mitsubishi MP-VF Lantern (Type 1, Horizontal/Vertical, Odd/Even)\n" +
									"*   Mitsubishi MP Button (Type 1)\n" +
									"*   Mitsubishi MP Touch Button (Type 1)\n" +
									"*   Mitsubishi PIM-A110 Call Panel\n" +
									"*   Mitsubishi HBM-A110 Call Panel\n" +
									"*   Mitsubishi PIN-A11 Call Panel\n" +
									"*   Mitsubishi HBN-A11 Call Panel\n" +
									"*   Mitsubishi PIH-D330 Screen (Odd/Even)\n" +
									"*   Mitsubishi Ryoden Screen (Type 2, Odd/Even)\n" +
									"*   Mitsubishi PIE-C210 & C220 Call Panel\n" +
									"*   Mitsubishi HBE-C210 & C220 Call Panel\n" +
									"*   Mitsubishi PI*-A634 & A644 Call Panel (and LCD version, Without Screen version)\n" +
									"*   Mitsubishi PI*-C634 & C644 Call Panel (and LCD version, Without Screen version)\n" +
									"*   Mitsubishi HLH-A31S Hall Lantern (Horizontal/Vertical, Odd/Even)\n" +
									"*   Mitsubishi HLV-A31S Hall Lantern (Horizontal/Vertical, Odd/Even)\n" +
									"*   Mitsubishi HLV-A21S Hall Lantern (Odd/Even)\n" +
									"*   Mitsubishi PIH-C110 Screen (Odd/Even)\n" +
									"*   Mitsubishi PIH-C130 Screen (Odd/Even)\n" +
									"\n" +
									"**Shanghai Mitsubishi Series**\n" +
									"*   Shanghai Mitsubishi ZPI*-GD10 & GD20 Call Panel\n" +
									"*   Shanghai Mitsubishi ZHB*-G010 Call Panel\n" +
									"*   Shanghai Mitsubishi ZPI*-CD12 & CD22 Call Panel\n" +
									"*   Shanghai Mitsubishi ZPI*-GA13 & GA23 Call Panel\n" +
									"*   Shanghai Mitsubishi ZPIH-C301 Hall Floor Indicator(Type 1) (Odd/Even)\n" +
									"*   Shanghai Mitsubishi ZPIH-CE01 Hall Floor Indicator(Type 1) (Odd/Even)\n" +
									"\n" +
									"**OTIS Series**\n" +
									"*   OTIS Series 1 Hall Lantern(Type 1) (Odd/Even)\n" +
									"*   OTIS Series 1 Hall Lantern(Type 1, With Screen) (Odd/Even)\n" +
									"\n" +
									"**Schindler Series**\n" +
									"*   Schindler S-Series Dark Grey Button Call Panel\n" +
									"*   Schindler S-Series Blue Button Call Panel\n" +
									"*   Schindler R-Series Round Button Call Panel\n" +
									"*   Schindler R-Series Hall Floor Indicator(Type 1) (Odd/Even)\n" +
									"*   Schindler FI GS 700 Call Panel(Without Screen)\n" +
									"*   Schindler FI GS 700 Touch Call Panel(Without Screen)\n" +
									"*   Schindler FI GS 700 Screen(Steel/Black/Grey) (Odd/Even)\n" +
									"\n" +
									"**Toshiba Series**\n" +
									"*   Toshiba Button (Type 1)\n" +
									"*   Toshiba Lantern (Type 1) (Odd/Even)\n" +
									"*   Toshiba Screen (Type 1/2) (Odd/Even)\n" +
									"\n" +
									"**Railway Facilities (PAT) Series**\n" +
									"*   PAT RS01 Railway Sign\n" +
									"*   PAT RS01 Railway Sign Pole\n" +
									"\n" +
									"### New Features\n" +
									"*   Added sound effects for some arrival indicators (hall lanterns).\n" +
									"*   Added arrow rotation animation for some Mitsubishi components.\n" +
									"\n" +
									"### Translation Updates for Features\n" +
									"*   English names for YTE tool items have been unified according to the new version. Chinese names remain unchanged.\n" +
									"*   The translation for `message.floor_auto_setter_status_need_track_floor_position` has been updated to: \"Please select an lift/elevator floor track.\"\n" +
									"\n" +
									"### New/Updated Tooltips\n" +
									"**Hitachi Series**\n" +
									"*   Display: HIP-31-2\n" +
									"*   Display: SCLC-LCD4\n" +
									"*   Button: ML-MW\n" +
									"*   Button: UL-MW\n" +
									"*   Changsha IFS Style\n" +
									"\n" +
									"**Shanghai Mitsubishi Series**\n" +
									"*   Display: ZLHH-110AG01\n" +
									"*   Display: ZLHH-110DG01\n" +
									"*   Display: ZLHH-101AG02\n" +
									"*   Button: A11\n" +
									"*   Button: A12\n" +
									"\n" +
									"### Improvements\n" +
									"*   Optimized custom font caching. Screens no longer flicker when the elevator floor changes.\n" +
									"\n" +
									"### Fixes\n" +
									"*   Fixed an issue where the Lift Floor Auto Setter would not work correctly with horizontal elevator tracks.",
							dependenciesModrinth,
							ReleaseStatus.BETA,
							Collections.singleton(minecraftVersion),
							Collections.singleton(modLoader),
							false,
							Files.newInputStream(filePath),
							fileName,
							args[1]
					));
				}
			}
		}
	}
}
