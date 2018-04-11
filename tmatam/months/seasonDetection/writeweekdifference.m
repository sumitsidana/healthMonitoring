#!/usr/bin/octave -qf
clear

listing = dir('training')
listinginfo = dir('distributions')
for i = 3:68
%     A =  dlmread(strcat('/media/toshibasecond/FullATAM/training/ailments/trainfile'),',')
%     B =  dlmread(strcat('/media/toshibasecond/FullATAM/training/ailments/testfile'),',')
    C =  dlmread(strcat('training/',listing(i).name,'/topics/trainfile'),',')
    D =  dlmread(strcat('training/',listing(i).name,'/topics/testfile'),',')
   
    
%     numRowsAilments = size(A,1)
    numRowsTopics = size(C,1)
    KlDivergenceAilments = zeros(numRowsTopics,1);
    for k = 1:numRowsTopics
                KlDivergenceAilments(k,:) = bhattacharyya(C(k,:),D(k,:))
%         KlDivergenceAilments(k,:) = KLDiv(A(k,:),B(k,:))
    end
    a = ''
    b = strcat('distributions/',listing(i).name,'/topics/')
    space = '';
    filepath1 = char([a space b])
    file = fopen(strcat(filepath1,'/','topicshistorical'))
    test = textscan(file,'%s','delimiter','\n','bufsize',50000)
    region = listing(i).name
    listofweeks = cell(length(test{1}),1)
    listweeks = cell(length(test{1}),1)
    for j = 1:length(test{1})
        line = test{1}{j}
        index = strfind(line,'#|#')
        year_week = line(1:index-1)
        index = strfind(year_week,'_')
%         datetime = strcat(year_week(1:index-1),'.',year_week(index+1:length(year_week)))
        datetime = year_week(index+1:length(year_week))
        listweeks{j} = datetime
        listofweeks{j} = year_week
    end
    
    for k = 1:length(listweeks)
        w = str2double(listweeks(k))
        week(k,:) = w
    end
    
    merged = [week KlDivergenceAilments];
    [Y,I]=sort(merged(:,2));
    sortedmerged=merged(I,:); %use the column indices from sort() to sort all columns of A.
     csvwrite(strcat('distributiondifferencedistancessorted/',listing(i).name),sortedmerged)
%     hold on
%      Figure = plot(sortedmerged(:,2));
%      NumTicks = length(listofweeks);
%      L = get(gca,'XLim');
%      set(gca,'XTickLabel',sortedmerged(:,1),'XTick',linspace(L(1),L(2),NumTicks))
%     
%      saveas(Figure,strcat('topkdifferences/',listing(i).name,'bh.png'))
%      close all
%      hold off
%     hold on
%     Figure = plot(KlDivergenceAilments);
%     NumTicks = length(listofweeks);
%     L = get(gca,'XLim');
%     set(gca,'XTickLabel',week,'XTick',linspace(L(1),L(2),NumTicks))
%     
%     saveas(Figure,strcat('topkdifferences/',listing(i).name,'kl.png'))
%     close all
%     hold off
    
    clear week
end

