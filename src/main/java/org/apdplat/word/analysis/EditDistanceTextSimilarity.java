/**
 *
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.apdplat.word.analysis;

import org.apdplat.word.segmentation.Word;

import java.util.List;

/**
 * 文本相似度计算
 * 判定方式：编辑距离（Edit Distance）
 * 指两个字串之间，由一个转成另一个所需的最少编辑操作次数
 * 允许的编辑操作包括将一个字符替换成另一个字符，增加一个字符，删除一个字符
 * 例如将kitten一字转成sitting：
 * sitten （k→s）将一个字符k替换成另一个字符s
 * sittin （e→i）将一个字符e替换成另一个字符i
 * sitting （→g）增加一个字符g
 * 因为这个算法是俄罗斯科学家Vladimir Levenshtein在1965年提出
 * 所以编辑距离（Edit Distance）又称Levenshtein距离
 * @author 杨尚川
 */
public class EditDistanceTextSimilarity extends TextSimilarity {
    /**
     * 计算相似度分值
     * @param words1 词列表1
     * @param words2 词列表2
     * @return 相似度分值
     */
    @Override
    protected double scoreImpl(List<Word> words1, List<Word> words2){
        //文本1
        StringBuilder text1 = new StringBuilder();
        words1.forEach(word -> text1.append(word.getText()));
        //文本2
        StringBuilder text2 = new StringBuilder();
        words2.forEach(word -> text2.append(word.getText()));
        //计算文本1和文本2的编辑距离
        int editDistance = editDistance(text1.toString(), text2.toString());
        int maxTextLength = Math.max(text1.length(), text2.length());
        double score = (1 - editDistance / (double)maxTextLength);
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("文本1和文本2的编辑距离："+editDistance);
            LOGGER.debug("文本1和文本2的最大长度："+maxTextLength);
            LOGGER.debug("文本1和文本2的相似度分值：1 - "+editDistance+" / (double)"+maxTextLength+"="+score);
        }
        return score;
    }

    private int editDistance(String text1, String text2) {
        int text1Length = text1.length();
        int text2Length = text2.length();
        if (text1Length == 0) {
            return text2Length;
        }
        if (text2Length == 0) {
            return text1Length;
        }
        int[][] matrix = new int[text1Length + 1][text2Length + 1];
        for (int i = 0; i <= text1Length; i++) {
            matrix[i][0] = i;
        }
        for (int j = 0; j <= text2Length; j++) {
            matrix[0][j] = j;
        }
        char char1;
        char char2;
        int cost;
        for (int i = 1; i <= text1Length; i++) {
            char1 = text1.charAt(i - 1);
            for (int j = 1; j <= text2Length; j++) {
                char2 = text2.charAt(j - 1);
                if (char1 == char2) {
                    cost = 0;
                } else {
                    cost = 1;
                }
                matrix[i][j] = min(matrix[i - 1][j] + 1, matrix[i][j - 1] + 1, matrix[i - 1][j - 1] + cost);
            }
        }
        return matrix[text1Length][text2Length];
    }

    /**
     * 求多个值中的最小值
     * @param nums 多个值
     * @return 最小值
     */
    private int min(int... nums) {
        int min = Integer.MAX_VALUE;
        for(int num : nums){
            min = Math.min(min, num);
        }
        return min;
    }

    public static void main(String[] args) {
        String text1 = "我爱购物";
        String text2 = "我爱读书";
        String text3 = "他是黑客";
        TextSimilarity textSimilarity = new EditDistanceTextSimilarity();
        double score1pk1 = textSimilarity.similarScore(text1, text1);
        double score1pk2 = textSimilarity.similarScore(text1, text2);
        double score1pk3 = textSimilarity.similarScore(text1, text3);
        double score2pk2 = textSimilarity.similarScore(text2, text2);
        double score2pk3 = textSimilarity.similarScore(text2, text3);
        double score3pk3 = textSimilarity.similarScore(text3, text3);
        System.out.println(text1+" 和 "+text1+" 的相似度分值："+score1pk1);
        System.out.println(text1+" 和 "+text2+" 的相似度分值："+score1pk2);
        System.out.println(text1+" 和 "+text3+" 的相似度分值："+score1pk3);
        System.out.println(text2+" 和 "+text2+" 的相似度分值："+score2pk2);
        System.out.println(text2+" 和 "+text3+" 的相似度分值："+score2pk3);
        System.out.println(text3+" 和 "+text3+" 的相似度分值："+score3pk3);
    }
}
